package com.example.paymentApi.settlement;


import com.example.paymentApi.ledgers.AccountRepository;
import com.example.paymentApi.ledgers.LedgerService;
import com.example.paymentApi.reservations.ReservationRepository;
import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.shared.exception.*;
import com.example.paymentApi.shared.mapper.CircleWebhookMapper;
import com.example.paymentApi.shared.utility.RedisUtil;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.wallets.WalletService;
import com.example.paymentApi.webhook.circle.CircleInboundWebhookResponse;
import com.example.paymentApi.webhook.circle.WebhookInboundNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class InboundTransactionSettlement {

    private final ObjectMapper objectMapper;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final RedisUtil redisUtil;
    private final Settlement settlement;
    private final LedgerService ledgerService;
    private final AccountRepository accountRepository;

    private static final String COMPLETE_STATE = "COMPLETE";
    private static final String FAILURE_STATE = "FAILED";
    private static final String NOTIFICATION_TYPE = "transactions.inbound";


    @Transactional
    public void settleInboundTransactions(String rawPayload) {
        CircleInboundWebhookResponse payload;

        try {

            payload = objectMapper.readValue(rawPayload, CircleInboundWebhookResponse.class);
        } catch (Exception e) {
            log.error("Invalid webhook payload structure", e);
            return;
        }

        if (payload.getNotification() == null) {
            log.error("Webhook notification is null");
            return;
        }

        String notificationType = payload.getNotificationType();
        WebhookInboundNotification notification = payload.getNotification();
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

        if (!NOTIFICATION_TYPE.equals(notificationType)) return;

        boolean lock = redisUtil.acquireLock(circleWalletId);
        if (!lock) {
            return;
        }

        Wallet wallet = walletRepository.findByCircleWalletId(circleWalletId).orElseThrow();

        Wallet sourceWallet = walletRepository.findByAddress(sourceAddress);
        Wallet destinationWallet = walletRepository.findByAddress(destinationAddress);

        try {

            if (COMPLETE_STATE.equalsIgnoreCase(state) && sourceWallet == null && destinationWallet != null ) {

                boolean exist = transactionRepository.existsByReferenceId(referenceId);
                if (exist) return;

                BigDecimal balanceAfter = destinationWallet.getAvailableBalance().add(amounts);

                walletService.creditWallet(destinationWallet.getCircleWalletId(), amounts);

                settlement.settleExternalInbound(amounts,
                        destinationAddress,
                        transactionType,
                        providerTransactionId,
                        referenceId,
                        sourceAddress,
                        destinationWallet,
                        balanceAfter);

                ledgerService.postExternalInbound(amounts,
                        providerTransactionId,
                        destinationWallet.getAccount());


                log.info("External inbound transaction successfully processed and settled");

                return;

            }


            if (COMPLETE_STATE.equalsIgnoreCase(state) && sourceWallet != null && destinationWallet != null){

                boolean exist = transactionRepository.existsByReferenceId(referenceId);
                if (exist) return;

                BigDecimal sourceBalanceAfter = sourceWallet.getAvailableBalance().subtract(amounts);
                BigDecimal destinationBalanceAfter = destinationWallet.getAvailableBalance().add(amounts);

                walletService.creditWallet(destinationWallet.getCircleWalletId(), amounts);

                settlement.settleInternalInbound(amounts,
                        referenceId,
                        sourceAddress,
                        destinationAddress,
                        providerTransactionId,
                        transactionType,
                        destinationWallet,
                        sourceBalanceAfter,
                        destinationBalanceAfter);

                ledgerService.postInternalInbound(amounts,
                        providerTransactionId,
                        sourceWallet.getAccount(),
                        destinationWallet.getAccount());

                log.info("Internal Inbound Transaction successfully processed and settled");

                return;

            }

            if (FAILURE_STATE.equalsIgnoreCase(state) && sourceWallet != null && destinationWallet != null) {

                boolean exist = transactionRepository.existsByReferenceId(referenceId);
                if (exist) return;

                BigDecimal balanceAfter = wallet.getAvailableBalance().add(amounts);

                walletService.creditWallet(wallet.getCircleWalletId(), amounts);

                settlement.settleInternalInboundFailure(providerTransactionId,
                        referenceId,
                        wallet,
                        amounts,
                        destinationAddress,
                        balanceAfter);

                ledgerService.postInternalReversal(amounts,
                        sourceWallet.getAccount(),
                        providerTransactionId);

                log.info("Internal Inbound transaction failed, reversal have been issued and records updated");

            }

        } catch (ConstraintViolationException | DataIntegrityViolationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ExternalServiceException("Error processing webhook response", e);
        }
    }
}


            /**
             * Execution steps:
            parse webhook event
            fetch txn and check if txn been processed before using intent id
            lock row
            if webhook = success
            debit wallet
            create ledger
            set tnx to success
            release lock
            if webhook = failure
            release hold/reservation
            mark txn = failed
             */



