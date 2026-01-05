package com.example.paymentApi.walletToWallet.inbound;

import com.example.paymentApi.ledgers.LedgerRequest;
import com.example.paymentApi.ledgers.LedgerService;
import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.shared.mapper.WebhookMapper;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.TransactionRequest;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.wallets.WalletRequest;
import com.example.paymentApi.wallets.WalletService;
import com.example.paymentApi.webhook.circle.CircleInboundWebhookResponse;
import com.example.paymentApi.webhook.circle.WebhookInboundNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
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
    private final WebhookMapper webhookMapper;
    private final WalletRepository walletRepository;
    public InboundTransferService(ObjectMapper objectMapper, WalletService walletService,
                                  TransactionService transactionService, LedgerService ledgerService,
                                  TransactionRepository transactionRepository, WebhookMapper webhookMapper,
                                  WalletRepository walletRepository
                                  ) {
        this.objectMapper = objectMapper;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.ledgerService = ledgerService;
        this.transactionRepository = transactionRepository;
        this.webhookMapper = webhookMapper;
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
        BigDecimal amounts = webhookMapper.mapCircleAmountType(notification.getAmounts());
        String state = notification.getState();
        TransactionType transactionType = webhookMapper.mapCircleTransactionType(notification.getTransactionType());
        String referenceId = notification.getTxHash();


        if (!TRANSACTION_TYPE.equals(notificationType)) return;
        if (!COMPLETE_STATE.equalsIgnoreCase(state)) return;


        boolean exist = transactionRepository.existsByReferenceId(referenceId);
            if(exist){
                return;

            }


        Wallet wallet = walletRepository.findByCircleWalletId(circleWalletId);

            try {

                TransactionRequest transactionRequest = new TransactionRequest();
                transactionRequest.setType(transactionType);
                transactionRequest.setStatus(TransactionStatus.SUCCESS);
                transactionRequest.setTransferId(transferId);
                transactionRequest.setAmounts(amounts);
                transactionRequest.setReferenceId(referenceId);
                transactionRequest.setSourceAddress(wallet.getAddress());
                transactionRequest.setDestinationAddress(destinationAddress);
                transactionService.createTransactionRecord(transactionRequest, wallet);

                LedgerRequest request = new LedgerRequest();
                request.setEntryType(LedgerType.INBOUND_TRANSFER); // or get from wehbook
                request.setAmount(amounts);
                request.setProvider(ProviderType.CIRCLE);
                request.setAsset(AssetType.USDC); // or get from webhook
                request.setStatus(LedgerStatus.POSTED);
                request.setReferenceId(referenceId);
                request.setSourceAddress(wallet.getAddress());
                request.setDestinationAddress(destinationAddress);
                request.setSourceCurrency("USDC");
                request.setDestinationCurrency("USDC");
                ledgerService.createDoubleEntryLedger(request, wallet);

                walletService.creditWallet(wallet.getId(), amounts);


        } catch (DataIntegrityViolationException e) {
                log.info("Duplicate webhook ignored for referenceId {}", referenceId);
        }

         catch (Exception e) {
            log.error("Failed processing inbound transfer", e);
        }
    }
}
