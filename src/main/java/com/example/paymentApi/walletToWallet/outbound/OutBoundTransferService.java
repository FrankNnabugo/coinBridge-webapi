package com.example.paymentApi.walletToWallet.outbound;

import com.example.paymentApi.integration.circle.CircleWalletService;
import com.example.paymentApi.ledgers.Ledger;
import com.example.paymentApi.ledgers.LedgerRepository;
import com.example.paymentApi.ledgers.LedgerRequest;
import com.example.paymentApi.ledgers.LedgerService;
import com.example.paymentApi.reservations.Reservation;
import com.example.paymentApi.reservations.ReservationRepository;
import com.example.paymentApi.reservations.ReservationRequest;
import com.example.paymentApi.reservations.ReservationService;
import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.shared.exception.ValidationException;
import com.example.paymentApi.shared.utility.Verifier;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.TransactionRequest;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.wallets.WalletService;
import com.example.paymentApi.webhook.circle.CircleOutBoundWebhookResponse;
import com.example.paymentApi.webhook.circle.OutboundTransferInitiationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
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


    @Transactional
    public String initiateTransfer(OutBoundRequest outBoundRequest, String id,
                                   OutboundTransferInitiationResponse response) {
        if (outBoundRequest.getAmounts() == null
                || outBoundRequest.getDestinationAddress() == null
                || outBoundRequest.getBlockchain() == null) {
            throw new ValidationException("Amount, address, blockchain must be provided");
        }

        if (outBoundRequest.getAmounts().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        if (outBoundRequest.getAmounts().signum() <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        Verifier.validatePaymentInput(outBoundRequest.getDestinationAddress(), outBoundRequest.getBlockchain());

        Wallet wallet = walletRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Wallet does not exist"));


        TransactionRequest request = new TransactionRequest();
        request.setTransferId(response.getId());
        request.setType(TransactionType.OUTBOUND_TRANSFER);
        request.setAmounts(outBoundRequest.getAmounts());
        request.setStatus(TransactionStatus.PENDING);
        request.setSourceAddress(wallet.getAddress());
        request.setDestinationAddress(outBoundRequest.getDestinationAddress());

        transactionService.createTransactionRecord(request, wallet);

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setAmount(outBoundRequest.getAmounts());
        reservationRequest.setTransactionId(response.getId());
        reservationRequest.setReservationType(ReservationType.OUTBOUND_TRANSFER);

        reservationService.reserveFund(id, reservationRequest);

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(outBoundRequest.getAmounts()));
        wallet.setReservedBalance(wallet.getReservedBalance().add(outBoundRequest.getAmounts()));
        walletRepository.save(wallet);

        circleWalletService.createTransferIntent(id, outBoundRequest.getDestinationAddress(),
                outBoundRequest.getBlockchain(), outBoundRequest.getAmounts())
                .subscribe();

        return "Payment Successfully processed";

        //validate input
        //create txn = pending
        //persist intent id
        //create hold/reservation
        //call provider
        //no lock
        //no ledger
        //no debit
        //no release

    }


    @Transactional
    public void finalizeTransfer(String rawPayload, String id) {
        CircleOutBoundWebhookResponse payload;

        try {
            payload = objectMapper.readValue(rawPayload, CircleOutBoundWebhookResponse.class);
        } catch (JsonProcessingException e) {
            e.getMessage();
            throw new ValidationException("Invalid webhook payload structure");
        }

        String transferId = payload.getData().getNotification().getId();
        String walletId = payload.getData().getNotification().getWalletId();
        String sourceAddress = payload.getData().getNotification().getSourceAddress();
        String destinationAddress = payload.getData().getNotification().getDestinationAddress();
        String transactionType = payload.getData().getNotification().getTransactionType();
        String state = payload.getData().getNotification().getState();
        BigDecimal amount = payload.getData().getNotification().getAmount();
        String referenceId = payload.getData().getNotification().getTxHash();
        String fee = payload.getData().getNotification().getNetworkFee();

        if (transactionService.findTransactionByReferenceId(referenceId)) {
            return;
        }

        try {
            Wallet wallet = walletRepository.findByIdForUpdate(id).orElseThrow(() ->
                    new ResourceNotFoundException("Wallet does not exist"));

            Transactions transactions = transactionRepository.findByIdForUpdate(id).orElseThrow(() ->
                    new ResourceNotFoundException("Transaction record does not exist"));

            Reservation reservation = reservationRepository.findById(id).orElseThrow();

            if(reservation.getStatus() != ReservationStatus.ACTIVE) return;



            if ("COMPLETE".equalsIgnoreCase(state)) {
                walletService.debitWallet(id, amount);

                LedgerRequest request = new LedgerRequest();
                request.setEntryType(LedgerType.OUTBOUND_TRANSFER);
                request.setAmount(amount);
                request.setProvider(ProviderType.CIRCLE);
                request.setAsset(AssetType.USDC);
                request.setStatus(LedgerStatus.POSTED);
                request.setReferenceId(referenceId);
                request.setSourceAddress(sourceAddress);
                request.setDestinationAddress(destinationAddress);

                ledgerService.createDoubleEntryLedger(request, wallet);

                transactions.setReferenceId(referenceId);
                transactions.setStatus(TransactionStatus.SUCCESS);
                transactionRepository.save(transactions);

                reservation.setStatus(ReservationStatus.RELEASED);
                reservationRepository.save(reservation);

            }

                else {
                    walletService.creditWallet(id, amount);
                    reservation.setStatus(ReservationStatus.RELEASED);
                    reservationRepository.save(reservation);
                    transactions.setStatus(TransactionStatus.FAILED);
                    transactionRepository.save(transactions);
                }



            //TODO:
            //parse webhook event
            //fetch txn and check if txn been processed before using intent id
            //lock row
            //if webhook = success
            //debit wallet
            //create ledger
            //set tnx to success
            //release lock

            //if webhook = failure
            //release hold/reservation
            //mark txn = failed
            //notify user
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}

