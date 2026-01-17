package com.example.paymentApi.walletToWallet.inbound;

import com.example.paymentApi.ledgers.LedgerRequest;
import com.example.paymentApi.ledgers.LedgerService;
import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.shared.mapper.WebhookMapper;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.TransactionRequest;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.wallets.WalletService;
import com.example.paymentApi.webhook.circle.CircleInboundWebhookResponse;
import com.example.paymentApi.webhook.circle.WebhookInboundNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j
public class InboundTransferService {

    private static final String COMPLETE_STATE = "COMPLETE";
    private static final String TRANSACTION_TYPE = "transactions.inbound";
    private final ObjectMapper objectMapper;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final LedgerService ledgerService;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    public InboundTransferService(ObjectMapper objectMapper, WalletService walletService,
                                  TransactionService transactionService, LedgerService ledgerService,
                                  TransactionRepository transactionRepository,
                                  WalletRepository walletRepository
                                  ) {
        this.objectMapper = objectMapper;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.ledgerService = ledgerService;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void processInboundTransfer(String rawPayload) {

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

        WebhookInboundNotification notification = payload.getNotification();

        String notificationType = payload.getNotificationType();
        String transferId = notification.getId();
        String blockchain = notification.getBlockchain();
        String circleWalletId = notification.getWalletId();
        String tokenId = notification.getTokenId();
        String destinationAddress = notification.getDestinationAddress();
        String sourceAddress = notification.getSourceAddress();
        BigDecimal amounts = WebhookMapper.mapCircleAmountType(notification.getAmounts());
        String state = notification.getState();
        TransactionType transactionType = WebhookMapper.mapCircleTransactionType(notification.getTransactionType());
        String referenceId = notification.getTxHash();


        if (!TRANSACTION_TYPE.equals(notificationType)) return;
        if (!COMPLETE_STATE.equalsIgnoreCase(state)) return;

        try {

        Wallet wallet = walletRepository.findByCircleWalletIdForUpdate(circleWalletId);

        boolean exist = transactionRepository.existsByReferenceId(referenceId);
            if(exist){
                return;

            }

                TransactionRequest transactionRequest = new TransactionRequest();
                transactionRequest.setType(transactionType);
                transactionRequest.setStatus(TransactionStatus.SUCCESS);
                transactionRequest.setTransferId(transferId);
                transactionRequest.setAmounts(amounts);
                transactionRequest.setReferenceId(referenceId);
                transactionRequest.setSourceAddress(sourceAddress);
                transactionRequest.setDestinationAddress(destinationAddress);
                transactionService.createTransactionRecord(transactionRequest, wallet);

                BigDecimal balanceBefore = wallet.getAvailableBalance();
                BigDecimal balanceAfter = balanceBefore.add(amounts);

                LedgerRequest request = new LedgerRequest();
                request.setEntryType(LedgerType.INBOUND_TRANSFER); // or get from wehbook
                request.setAmount(amounts);
                request.setProvider(ProviderType.CIRCLE);
                request.setAsset(AssetType.USDC); // or get from webhook
                request.setStatus(LedgerStatus.POSTED);
                request.setReferenceId(referenceId);
                request.setSourceAddress(sourceAddress);
                request.setDestinationAddress(destinationAddress);
                request.setSourceCurrency("USDC");
                request.setDestinationCurrency("USDC");
                request.setBalanceBefore(balanceBefore);
                request.setBalanceAfter(balanceAfter);
                ledgerService.createDoubleEntryLedger(request, wallet);

                walletService.creditWallet(wallet.getId(), amounts);

            }
        catch(ConstraintViolationException | DataIntegrityViolationException e){
               log.info("Duplicate webhook event with referenceId {} ", referenceId + "Ignored");
            }

        catch (Exception e) {
            log.error("Failed processing inbound transfer", e);
        }
    }
}
