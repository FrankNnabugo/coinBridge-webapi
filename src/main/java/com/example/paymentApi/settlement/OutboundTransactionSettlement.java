package com.example.paymentApi.settlement;

import com.example.paymentApi.event.computeLedgerBalance.LedgerEntryEvent;
import com.example.paymentApi.event.computeLedgerBalance.LedgerEntryPublisher;
import com.example.paymentApi.ledgers.Ledger;
import com.example.paymentApi.ledgers.LedgerRepository;
import com.example.paymentApi.ledgers.LedgerService;
import com.example.paymentApi.reservations.Reservation;
import com.example.paymentApi.reservations.ReservationRepository;
import com.example.paymentApi.shared.enums.TransactionType;
import com.example.paymentApi.shared.exception.ExternalServiceException;
import com.example.paymentApi.shared.mapper.CircleWebhookMapper;
import com.example.paymentApi.shared.utility.RedisUtil;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.wallets.WalletService;
import com.example.paymentApi.webhook.circle.CircleOutboundWebhookResponse;
import com.example.paymentApi.webhook.circle.WebhookOutboundNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboundTransactionSettlement {

    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;
    private final Settlement settlement;
    private final WalletService walletService;
    private final ReservationRepository reservationRepository;
    private final WalletRepository walletRepository;
    private final LedgerService ledgerService;
    private final LedgerEntryPublisher ledgerEntryPublisher;
    private final TransactionRepository transactionRepository;
    private final LedgerRepository ledgerRepository;

    private static final String COMPLETE_STATE = "COMPLETE";
    private static final String FAILURE_STATE = "FAILED";
    private static final String NOTIFICATION_TYPE = "transactions.outbound";

    public void settleOutboundTransactions(String rawPayload) {

        CircleOutboundWebhookResponse payload;

        try {

            payload = objectMapper.readValue(rawPayload, CircleOutboundWebhookResponse.class);
        } catch (Exception e) {
            log.error("Invalid webhook payload structure", e);
            return;
        }

        if (payload.getNotification() == null) {
            log.error("Webhook notification is null");
            return;
        }

        String notificationType = payload.getNotificationType();
        WebhookOutboundNotification notification = payload.getNotification();
        String providerTransactionId = notification.getId();
        String blockchain = notification.getBlockchain();
        String circleWalletId = notification.getWalletId();
        String tokenId = notification.getTokenId();
        String sourceAddress = notification.getSourceAddress();
        String destinationAddress = notification.getDestinationAddress();
        TransactionType transactionType = CircleWebhookMapper.mapCircleTransactionType(notification.getTransactionType());
        String state = notification.getState();
        BigDecimal amounts = CircleWebhookMapper.mapCircleAmountType(notification.getAmounts());
        String referenceId = notification.getTxHash();
        String fee = notification.getNetworkFee();

        if (!NOTIFICATION_TYPE.equalsIgnoreCase(notificationType)) return;
        if(!COMPLETE_STATE.equalsIgnoreCase(state) && !FAILURE_STATE.equalsIgnoreCase(state)) return;

        Wallet sourceWallet = walletRepository.findByAddress(sourceAddress).orElse(null);
        Wallet destinationWallet = walletRepository.findByAddress(destinationAddress).orElse(null);
        Reservation reservation = reservationRepository.findByProviderTransactionId(providerTransactionId);

        if (COMPLETE_STATE.equalsIgnoreCase(state) &&
                sourceWallet != null
                && destinationWallet != null
                && reservation != null) return;

        boolean lock = redisUtil.acquireLock(circleWalletId);
        if (!lock) {
            return;
        }

        try {

            if (COMPLETE_STATE.equalsIgnoreCase(state)
                    && sourceWallet != null
                    && destinationWallet == null
                    && reservation != null) {

                boolean exist = transactionRepository.existsByReferenceId(referenceId);
                if (exist) return;

                settlement.settleOutboundTransaction(providerTransactionId,
                        referenceId);

                ledgerService.postInternalToExternal(amounts,
                        sourceWallet.getAccount(),
                        providerTransactionId);

                Transactions transaction = transactionRepository.findByProviderTransactionId(providerTransactionId);
                Ledger ledger = ledgerRepository.findByTransactionId(transaction.getId());
                ledgerEntryPublisher.publishLedgerEntryCreatedEvent(new LedgerEntryEvent(ledger.getAccount()));

                log.info("Internal to External Wallet transaction successfully processed and settled");

                return;

            }

            if (FAILURE_STATE.equalsIgnoreCase(state)
                    && sourceWallet != null
                    && destinationWallet == null
                    && reservation != null) {

                boolean exist = transactionRepository.existsByReferenceId(referenceId);
                if (exist) return;

                walletService.creditWallet(sourceWallet.getCircleWalletId(), amounts);

                settlement.settleOutboundTransactionFailure(providerTransactionId, referenceId, sourceWallet,
                        amounts,
                        sourceAddress,
                        destinationAddress);

                ledgerService.postInternalToExternalFailure(amounts,
                        sourceWallet.getAccount(),
                        providerTransactionId);

                Transactions transaction = transactionRepository.findByProviderTransactionId(providerTransactionId);
                Ledger ledger = ledgerRepository.findByTransactionId(transaction.getId());
                ledgerEntryPublisher.publishLedgerEntryCreatedEvent(new LedgerEntryEvent(ledger.getAccount()));

                log.info("Internal to External Wallet transaction failed, reversal issued and records updated");
            }
        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ExternalServiceException("Error processing webhook response", e);
        }
    }
}
