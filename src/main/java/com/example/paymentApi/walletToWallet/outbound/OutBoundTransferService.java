package com.example.paymentApi.walletToWallet.outbound;

import com.example.paymentApi.event.transfer.TransferInitiationFailedEvent;
import com.example.paymentApi.event.transfer.TransferInitiationFailedPublisher;
import com.example.paymentApi.integration.circle.CircleWalletService;
import com.example.paymentApi.ledgers.LedgerRequest;
import com.example.paymentApi.ledgers.LedgerService;
import com.example.paymentApi.reservations.Reservation;
import com.example.paymentApi.reservations.ReservationRepository;
import com.example.paymentApi.reservations.ReservationRequest;
import com.example.paymentApi.reservations.ReservationService;
import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.shared.exception.InsufficientBalanceException;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.shared.exception.ValidationException;
import com.example.paymentApi.shared.mapper.WebhookMapper;
import com.example.paymentApi.shared.utility.Verifier;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.TransactionRequest;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.wallets.WalletService;
import com.example.paymentApi.webhook.circle.CircleOutBoundWebhookResponse;
import com.example.paymentApi.webhook.circle.OutBoundPayload;
import com.example.paymentApi.webhook.circle.OutboundTransferInitiationResponse;
import com.example.paymentApi.worker.paymentInitiation.OutboundRetryService;
import com.example.paymentApi.worker.paymentInitiation.OutboundRetryWorker;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutBoundTransferService {

    private final ObjectMapper objectMapper;
    private final TransactionService transactionService;
    private final WalletRepository walletRepository;
    private final ReservationService reservationService;
    private final CircleWalletService circleWalletService;
    private final TransactionRepository transactionRepository;
    private final ReservationRepository reservationRepository;
    private final WalletService walletService;
    private final LedgerService ledgerService;
    private final TransferInitiationFailedPublisher transferInitiationFailedPublisher;

    private static final String COMPLETE_STATE = "COMPLETE";
    private static final String FAILURE_STATE = "FAILED";
    private static final String TRANSACTION_TYPE = "transactions.outbound";


    @Transactional
    public String initiateTransfer(OutBoundRequest outBoundRequest, String id) {

        Verifier.validateAllInput(outBoundRequest.getDestinationAddress(),
                outBoundRequest.getBlockchain(), outBoundRequest.getAmounts());

        if (outBoundRequest.getAmounts().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        if (outBoundRequest.getAmounts().signum() <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        Verifier.validatePaymentInput(outBoundRequest.getDestinationAddress(), outBoundRequest.getBlockchain());

        Wallet wallet = walletRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Wallet does not exist"));


        OutboundTransferInitiationResponse response = new OutboundTransferInitiationResponse();

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setTransferId(response.getId());
        transactionRequest.setType(TransactionType.OUTBOUND_TRANSFER);
        transactionRequest.setAmounts(outBoundRequest.getAmounts());
        transactionRequest.setStatus(TransactionStatus.PENDING);
        transactionRequest.setSourceAddress(wallet.getAddress());
        transactionRequest.setDestinationAddress(outBoundRequest.getDestinationAddress());

        transactionService.createTransactionRecord(transactionRequest, wallet);


        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setAmount(outBoundRequest.getAmounts());
        reservationRequest.setTransactionId(response.getId());
        reservationRequest.setReservationType(ReservationType.OUTBOUND_TRANSFER);
        reservationRequest.setReason(ReservationReason.TRANSACTION_INITIATED);

        reservationService.reserveFund(wallet, reservationRequest);

        if (wallet.getAvailableBalance().compareTo(outBoundRequest.getAmounts()) < 0) {

            throw new InsufficientBalanceException("Insufficient available balance");

        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(outBoundRequest.getAmounts()));
        wallet.setReservedBalance(wallet.getReservedBalance().add(outBoundRequest.getAmounts()));
        walletRepository.save(wallet);

        /**
         *
         *
         Things to consider on transfer initiation failure
         Release hold and let transaction and reservation reflect this event
         Or
         */
        try {
            circleWalletService.createTransferIntent(id, outBoundRequest.getDestinationAddress(),
                            outBoundRequest.getBlockchain(), outBoundRequest.getAmounts())

                    .doOnSuccess(initiationResponse -> {
                        log.info("payment successfully initiated with response {}", initiationResponse);

                    })

                    .onErrorResume(error -> {
                        TransferInitiationFailedEvent event = new TransferInitiationFailedEvent(id, outBoundRequest);
                        transferInitiationFailedPublisher.publishTransferInitiationFailedEvent(event);
                        return null;
                    })
                    .subscribe();

        }
        catch (Exception e) {
            log.error("Error initiating payment", e);
        }

        return "Payment Successfully Processed";

        /**
         Execution steps:
         validate input
         create txn = pending
         persist intent id
         create hold/reservation
         call provider
         no lock
         no ledger
         no debit
         no release
         */
    }



    @Transactional
    public void finalizeTransfer(String rawPayload) {
        CircleOutBoundWebhookResponse payload;

        try {

            payload = objectMapper.readValue(rawPayload, CircleOutBoundWebhookResponse.class);
        }
        catch (Exception e){
            log.error("Invalid webhook payload structure", e);
            return;
        }

        if (payload.getData().getNotification() == null) {
        log.error("Webhook notification is null");
        return;
    }
        String notificationType = payload.getNotificationType();
        OutBoundPayload notification = payload.getData().getNotification();
        String transferId = notification.getId();
        String circleWalletId = notification.getWalletId();
        String sourceAddress = notification.getSourceAddress();
        String destinationAddress = notification.getDestinationAddress();
        TransactionType transactionType = WebhookMapper.mapCircleTransactionType(notification.getTransactionType());
        String state = notification.getState();
        BigDecimal amounts = WebhookMapper.mapCircleAmountType(notification.getAmount());
        String referenceId = notification.getTxHash();
        String fee = notification.getNetworkFee();

        if (!TRANSACTION_TYPE.equalsIgnoreCase(notificationType)) return;
        if (!COMPLETE_STATE.equalsIgnoreCase(state)) return;

        Wallet wallet = walletRepository.findByCircleWalletIdForUpdate(circleWalletId);

        try {

        boolean exist = transactionRepository.existsByReferenceId(referenceId);
                if(exist){
            return;
        }


            Transactions transactions = transactionRepository.findByTransferId(transferId);

            Reservation reservation = reservationRepository.findByTransactionId(transferId);

            if(reservation.getStatus() != ReservationStatus.ACTIVE) return;


            walletService.debitWallet(circleWalletId, amounts);

            BigDecimal balanceBefore = wallet.getAvailableBalance();
            BigDecimal balanceAfter = balanceBefore.add(amounts);

                LedgerRequest request = new LedgerRequest();
                request.setEntryType(LedgerType.OUTBOUND_TRANSFER);
                request.setAmount(amounts);
                request.setProvider(ProviderType.CIRCLE);
                request.setAsset(AssetType.USDC);
                request.setStatus(LedgerStatus.POSTED);
                request.setReferenceId(referenceId);
                request.setSourceAddress(sourceAddress);
                request.setDestinationAddress(destinationAddress);
                request.setSourceCurrency("USDC");
                request.setDestinationCurrency("USDC");
                request.setBalanceBefore(balanceBefore);
                request.setBalanceAfter(balanceAfter);

                ledgerService.createDoubleEntryLedger(request, wallet);

                transactions.setReferenceId(referenceId);
                transactions.setStatus(TransactionStatus.SUCCESS);
                transactions.setType(transactionType);
                transactionRepository.save(transactions);

                reservation.setStatus(ReservationStatus.RELEASED);
                reservation.setReason(ReservationReason.TRANSACTION_SUCCEEDED);
                reservationRepository.save(reservation);


                if(FAILURE_STATE.equalsIgnoreCase(state)){

                    /**
                     * if money moved, refund user, otherwise release only hold
                if (wallet.getReservedBalance().compareTo(amounts) < 0) {
                    throw new InsufficientBalanceException("Insufficient reserved balance");
                }
                     */

              walletService.creditWallet(circleWalletId, amounts);

                wallet.setReservedBalance(wallet.getReservedBalance().subtract(amounts));
                wallet.setAvailableBalance(wallet.getAvailableBalance().add(amounts));
                walletRepository.save(wallet);

                reservation.setStatus(ReservationStatus.RELEASED);
                reservation.setReason(ReservationReason.TRANSACTION_FAILED);
                reservationRepository.save(reservation);

                transactions.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transactions);

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

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}

