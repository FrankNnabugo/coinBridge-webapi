package com.example.paymentApi.walletToWallet.inbound;

import com.example.paymentApi.ledgers.LedgerRequest;
import com.example.paymentApi.ledgers.LedgerService;
import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.shared.exception.ValidationException;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.TransactionRequest;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.wallets.WalletService;
import com.example.paymentApi.webhook.circle.CircleInboundWebhookResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class InboundTransferService {

    private final ObjectMapper objectMapper;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final LedgerService ledgerService;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public InboundTransferService(ObjectMapper objectMapper, WalletService walletService,
                                  TransactionService transactionService, LedgerService ledgerService,
                                  TransactionRepository transactionRepository,WalletRepository walletRepository
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

        } catch (JsonProcessingException e) {
            e.getMessage();
            throw new ValidationException("Invalid webhook payload structure");
        }


        try {
            String notificationType = payload.getNotificationType();
            String transferId = payload.getNotification().getId();
            String blockchain = payload.getNotification().getBlockchain();
            String walletId = payload.getNotification().getWalletId();
            String tokenId = payload.getNotification().getTokenId();
            String destinationAddress = payload.getNotification().getDestinationAddress();
            BigDecimal amounts = payload.getNotification().getAmounts();
            String state = payload.getNotification().getState();
            TransactionType transactionType = payload.getNotification().getTransactionType();
            String referenceId = payload.getNotification().getTxHash();


            if (transactionService.findTransactionByReferenceId(referenceId)) {
                return;
            }
            // otherwise lock wallet here

            transactionRepository.findByReferenceIdForLock(referenceId);


           Wallet wallet = walletService.findByCircleWalletId(walletId);

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

            TransactionRequest transactionRequest = new TransactionRequest();
            transactionRequest.setType(transactionType);
            transactionRequest.setStatus(TransactionStatus.SUCCESS);
            transactionRequest.setTransferId(transferId);
            transactionRequest.setAmounts(amounts);
            transactionRequest.setReferenceId(referenceId);
            transactionRequest.setSourceAddress(wallet.getAddress());
            transactionRequest.setDestinationAddress(destinationAddress);


            if ("COMPLETED".equalsIgnoreCase(state) &&
                    "transactions.inbound".equalsIgnoreCase(notificationType)) {

                ledgerService.createDoubleEntryLedger(request, wallet);

                walletService.creditWallet(walletId, amounts);

                transactionService.createTransactionRecord(transactionRequest, wallet);

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
