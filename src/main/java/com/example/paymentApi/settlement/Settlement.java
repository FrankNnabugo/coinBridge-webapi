package com.example.paymentApi.settlement;

import com.example.paymentApi.reservations.Reservation;
import com.example.paymentApi.reservations.ReservationRepository;
import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.TransactionRequest;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class Settlement {

   private final WalletRepository walletRepository;
   private final TransactionService transactionService;
   private final TransactionRepository transactionRepository;
   private final ReservationRepository reservationRepository;


    public void settleExternalInbound(BigDecimal amounts,
                                      String destinationAddress,
                                      TransactionType transactionType,
                                      String providerTransactionId,
                                      String referenceId,
                                      String sourceAddress,
                                      Wallet destinationWallet){

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setUser(destinationWallet.getUser());
        transactionRequest.setWallet(destinationWallet);
        transactionRequest.setType(transactionType);
        transactionRequest.setStatus(TransactionStatus.SUCCESS);
        transactionRequest.setProviderTransactionId(providerTransactionId);
        transactionRequest.setAmounts(amounts);
        transactionRequest.setReferenceId(referenceId);
        transactionRequest.setSourceAddress(sourceAddress);
        transactionRequest.setDestinationAddress(destinationAddress);
        transactionRequest.setDirection(TransactionDirection.CREDIT);
        transactionRequest.setSourceCurrency(CurrencyType.USDC);
        transactionRequest.setDestinationCurrency(CurrencyType.USDC);

        transactionService.createTransactionRecord(transactionRequest);

    }


    public void settleInternalInbound(BigDecimal amounts,
                                      String referenceId,
                                      String sourceAddress,
                                      String destinationAddress,
                                      String providerTransactionId,
                                      TransactionType transactionType, Wallet destinationWallet){

        Transactions transaction = transactionRepository.findByProviderTransactionId(providerTransactionId);
        Reservation reservation = reservationRepository.findByProviderTransactionId(providerTransactionId);


        //source side and outgoing
        transaction.setReferenceId(referenceId);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservation.setReason(ReservationReason.TRANSACTION_SUCCEEDED);
        reservationRepository.save(reservation);

        //destination side and incoming
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setWallet(destinationWallet);
        transactionRequest.setUser(destinationWallet.getUser());
        transactionRequest.setType(transactionType);
        transactionRequest.setStatus(TransactionStatus.SUCCESS);
        transactionRequest.setProviderTransactionId(providerTransactionId);
        transactionRequest.setAmounts(amounts);
        transactionRequest.setReferenceId(referenceId);
        transactionRequest.setSourceAddress(sourceAddress);
        transactionRequest.setDestinationAddress(destinationAddress);
        transactionRequest.setDirection(TransactionDirection.CREDIT);
        transactionRequest.setSourceCurrency(CurrencyType.USDC);
        transactionRequest.setDestinationCurrency(CurrencyType.USDC);
        transactionService.createTransactionRecord(transactionRequest);


    }


    public void settleInternalInboundFailure(String providerTransactionId,
                                             String referenceId,
                                             Wallet wallet,
                                             BigDecimal amounts,
                                             String destinationAddress){

        Reservation reservation = reservationRepository.findByProviderTransactionId(providerTransactionId);
        Transactions transaction = transactionRepository.findByProviderTransactionId(providerTransactionId);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservation.setReason(ReservationReason.TRANSACTION_FAILED);
        reservationRepository.save(reservation);

        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setReferenceId(referenceId);
        transactionRepository.save(transaction);

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setWallet(wallet);
        transactionRequest.setUser(wallet.getUser());
        transactionRequest.setType(TransactionType.REVERSAL);
        transactionRequest.setStatus(TransactionStatus.SUCCESS);
        transactionRequest.setProviderTransactionId(providerTransactionId);
        transactionRequest.setAmounts(amounts);
        transactionRequest.setReferenceId(referenceId);
        transactionRequest.setSourceAddress("@Internal");
        transactionRequest.setDestinationAddress(destinationAddress);
        transactionRequest.setDirection(TransactionDirection.CREDIT);
        transactionRequest.setSourceCurrency(CurrencyType.USDC);
        transactionRequest.setDestinationCurrency(CurrencyType.USDC);

        transactionService.createTransactionRecord(transactionRequest);

    }

    public void settleOutboundTransaction(String providerTransactionId, String referenceId){

        Transactions transaction = transactionRepository.findByProviderTransactionId(providerTransactionId);
        Reservation reservation = reservationRepository.findByProviderTransactionId(providerTransactionId);

        transaction.setReferenceId(referenceId);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservation.setReason(ReservationReason.TRANSACTION_SUCCEEDED);
        reservationRepository.save(reservation);

    }
}
