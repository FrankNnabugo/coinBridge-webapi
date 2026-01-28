package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.transfer.TransferInitiationFailedEvent;
import com.example.paymentApi.event.transfer.TransferInitiationFailedPublisher;
import com.example.paymentApi.event.transfer.TransferInitiationPermanentlyFailedEvent;
import com.example.paymentApi.reservations.Reservation;
import com.example.paymentApi.reservations.ReservationRepository;
import com.example.paymentApi.shared.enums.ReservationReason;
import com.example.paymentApi.shared.enums.ReservationStatus;
import com.example.paymentApi.shared.enums.TransactionStatus;
import com.example.paymentApi.shared.exception.InternalProcessingException;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.shared.mapper.TypeMapper;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.worker.paymentInitiation.OutboundRetryService;
import com.example.paymentApi.worker.paymentInitiation.OutboundRetryWorker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransferInitiationFailedEventListener {

    private final TransferInitiationFailedPublisher transferInitiationFailedPublisher;
    private final OutboundRetryService outboundRetryService;
    private final OutboundRetryWorker outboundRetryWorker;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ReservationRepository reservationRepository;

    @EventListener
    public void handleTransferInitiationFailedEvent(TransferInitiationFailedEvent event){

        try{
            outboundRetryService.createPaymentRetryRecord(event.getUserId());
            outboundRetryWorker.retryPaymentInitiation(event.getOutBoundRequest(), event.getUserId(), event.getReservationId(),
                    event.getTransactionId());
        }
        catch (Exception e) {
            throw new InternalProcessingException("Error occurred", e);
        }

    }

    @Async
    @Transactional
    @EventListener
    public void handleTransferInitiationPermanentlyFailed(TransferInitiationPermanentlyFailedEvent event) {

        BigDecimal amounts = TypeMapper.convertToBigDecimal(event.getAmounts());

        try {

            Wallet wallet = walletRepository.findByCircleWalletId(event.getCircleWalletId()).orElseThrow(() ->
                    new ResourceNotFoundException("Wallet does not exist"));

            Reservation reservation = reservationRepository.findById(event.getReservationId())
                    .orElseThrow();

            Transactions transaction = transactionRepository.findById(event.getTransactionId())
                    .orElseThrow();

            if (transaction.getStatus() == TransactionStatus.SUCCESS) {
                return;
            }

            if (reservation.getStatus() != ReservationStatus.ACTIVE) {
                return;
            }

            wallet.setReservedBalance(wallet.getReservedBalance().subtract(amounts));
            wallet.setAvailableBalance(wallet.getAvailableBalance().add(amounts));
            walletRepository.save(wallet);

            reservation.setStatus(ReservationStatus.RELEASED);
            reservation.setReason(ReservationReason.TRANSACTION_FAILED);
            reservationRepository.save(reservation);

            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            log.info("Hold released, status updated");


        } catch (Exception e) {
            throw new InternalProcessingException("Error occurred while releasing hold", e);
        }

        /**
         *
         *Things to consider on transfer initiation retry failure:
         *
         publish transfer permanently failed event
         /**
         *
         *
         Listener calls for hold release and let transaction and reservation reflect this event
         update reservation status to release and reason to transaction_failed
         update transaction record to failed
         every state balances to its initial state before the transaction
         */
        
    }
}
