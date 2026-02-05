package com.example.paymentApi.settlement;

import com.example.paymentApi.ledgers.LedgerService;
import com.example.paymentApi.reservations.Reservation;
import com.example.paymentApi.reservations.ReservationRepository;
import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.TransactionRequest;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.wallets.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class Settlement {

   private final TransactionService transactionService;
   private final TransactionRepository transactionRepository;
   private final ReservationRepository reservationRepository;
   private final LedgerService ledgerService;


   @Transactional
    public void settleExternalInbound(BigDecimal amounts,
                                      String destinationAddress,
                                      TransactionType transactionType,
                                      String providerTransactionId,
                                      String referenceId,
                                      String sourceAddress,
                                      Wallet destinationWallet,
                                      BigDecimal balanceAfter){

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
        transactionRequest.setBalanceAfter(balanceAfter);

        transactionService.createTransactionRecord(transactionRequest);


    }


    public void settleInternalInbound(BigDecimal amounts,
                                      String referenceId,
                                      String sourceAddress,
                                      String destinationAddress,
                                      String providerTransactionId,
                                      TransactionType transactionType,
                                      Wallet destinationWallet,
                                      BigDecimal sourceBalanceAfter,
                                      BigDecimal destinationBalanceAfter){

        Transactions transaction = transactionRepository.findByProviderTransactionId(providerTransactionId);
        Reservation reservation = reservationRepository.findByProviderTransactionId(providerTransactionId);


        //source side and outgoing
        transaction.setReferenceId(referenceId);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setBalanceAfter(sourceBalanceAfter);
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
        transactionRequest.setBalanceAfter(destinationBalanceAfter);
        transactionService.createTransactionRecord(transactionRequest);


    }


    public void settleInternalInboundFailure(String providerTransactionId,
                                             String referenceId,
                                             Wallet wallet,
                                             BigDecimal amounts,
                                             String destinationAddress,
                                             BigDecimal balanceAfter){

        Reservation reservation = reservationRepository.findByProviderTransactionId(providerTransactionId);
        Transactions transaction = transactionRepository.findByProviderTransactionId(providerTransactionId);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservation.setReason(ReservationReason.TRANSACTION_FAILED);
        reservationRepository.save(reservation);

        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setReferenceId(referenceId);
        transaction.setBalanceAfter(balanceAfter);
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

    public void settleOutboundTransaction(String providerTransactionId,
                                          String referenceId,
                                          BigDecimal balanceAfter){

        Transactions transaction = transactionRepository.findByProviderTransactionId(providerTransactionId);
        Reservation reservation = reservationRepository.findByProviderTransactionId(providerTransactionId);

        transaction.setReferenceId(referenceId);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setBalanceAfter(balanceAfter);
        transactionRepository.save(transaction);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservation.setReason(ReservationReason.TRANSACTION_SUCCEEDED);
        reservationRepository.save(reservation);

    }

    public void settleOutboundTransactionFailure(String providerTransactionId,
                                                 String referenceId,
                                                 Wallet wallet,
                                                 BigDecimal amounts,
                                                 String destinationAddress,
                                                 BigDecimal initialBalanceAfter,
                                                 BigDecimal reversalBalanceAfter){

        Reservation reservation = reservationRepository.findByProviderTransactionId(providerTransactionId);
        Transactions transaction = transactionRepository.findByProviderTransactionId(providerTransactionId);

        reservation.setStatus(ReservationStatus.RELEASED);
        reservation.setReason(ReservationReason.TRANSACTION_FAILED);
        reservationRepository.save(reservation);

        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setReferenceId(referenceId);
        transaction.setBalanceAfter(initialBalanceAfter);
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
        transactionRequest.setBalanceAfter(reversalBalanceAfter);
        transactionService.createTransactionRecord(transactionRequest);

    }
}
