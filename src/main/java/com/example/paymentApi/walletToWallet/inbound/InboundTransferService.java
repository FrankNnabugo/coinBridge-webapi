package com.example.paymentApi.walletToWallet.inbound;

import com.example.paymentApi.shared.exception.ValidationException;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletService;
import com.example.paymentApi.webhook.circle.CircleInboundWebhookResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class InboundTransferService {

    private final ObjectMapper objectMapper;
    private final WalletService walletService;
    private final TransactionService transactionService;

    public InboundTransferService(ObjectMapper objectMapper, WalletService walletService,
                                  TransactionService transactionService) {
        this.objectMapper = objectMapper;
        this.walletService = walletService;
        this.transactionService = transactionService;
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

//        String transferId = payload.getNotification().getId();
//        //String walletId = payload.getNotification().getCircleWalletId();
//        String address = payload.getNotification().getDestinationAddress();
//        BigDecimal amount = payload.getNotification().getAmounts();
//        String state = payload.getNotification().getState();
//        String transactionType = payload.getNotification().getTransactionType();
//


        if (transactionService.findTransactionByTransferId(payload.getNotification().getId())) {
            return;
        }

        Wallet wallet = walletService.findByCircleWalletId(payload.getNotification().getCircleWalletId());


        if ("CONFIRMED".equalsIgnoreCase(payload.getNotification().getState()) &&
                "transactions.inbound".equalsIgnoreCase(payload.getNotificationType())) {

            walletService.creditWallet(payload.getNotification().getCircleWalletId(), payload.getNotification().getAmounts());

            transactionService.createTransactionRecord(payload, wallet);

        }

    }
}
