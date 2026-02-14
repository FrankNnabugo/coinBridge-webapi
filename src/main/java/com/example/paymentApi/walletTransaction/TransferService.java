package com.example.paymentApi.walletTransaction;

import com.example.paymentApi.event.transfer.TransferInitiationFailedEvent;
import com.example.paymentApi.event.transfer.TransferInitiationFailedPublisher;
import com.example.paymentApi.integration.circle.CircleWalletService;
import com.example.paymentApi.reservations.Reservation;
import com.example.paymentApi.reservations.ReservationRepository;
import com.example.paymentApi.reservations.ReservationRequest;
import com.example.paymentApi.reservations.ReservationService;
import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.shared.exception.ExternalServiceException;
import com.example.paymentApi.shared.exception.InsufficientBalanceException;
import com.example.paymentApi.shared.exception.ValidationException;
import com.example.paymentApi.shared.mapper.TypeMapper;
import com.example.paymentApi.shared.utility.RedisUtil;
import com.example.paymentApi.shared.utility.Verifier;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.transaction.TransactionRequest;
import com.example.paymentApi.transaction.TransactionService;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.wallets.WalletService;
import com.example.paymentApi.worker.paymentInitiation.OutboundRetryService;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final TransactionService transactionService;
    private final WalletRepository walletRepository;
    private final ReservationService reservationService;
    private final CircleWalletService circleWalletService;
    private final TransactionRepository transactionRepository;
    private final ReservationRepository reservationRepository;
    private final TransferInitiationFailedPublisher transferInitiationFailedPublisher;
    private final RedisUtil redisUtil;
    private final OutboundRetryService outboundRetryService;


    @Transactional
    public TransferResponse initiateTransfer(TransferRequest request, String userId) {

        Verifier.validateAllInput(request.getDestinationAddress(), request.getBlockchain(), request.getAmounts());

        BigDecimal amounts = TypeMapper.convertToBigDecimal(request.getAmounts());

        if (amounts.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        if (amounts.signum() <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        Verifier.validatePaymentInput(request.getDestinationAddress(), request.getBlockchain());

        Wallet wallet = walletRepository.findByUser_id(userId);

        if (wallet.getAvailableBalance().compareTo(amounts) < 0) {

            throw new InsufficientBalanceException("Insufficient available balance");

        }

        boolean lock = redisUtil.acquireLock(wallet.getCircleWalletId());
        if (!lock) {
            TransferResponse response = new TransferResponse();
            response.setMessage("Transfer already being processed");
            return response;
        }

        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setWallet(wallet);
        transactionRequest.setUser(wallet.getUser());
        transactionRequest.setType(TransactionType.OUTBOUND_TRANSFER);
        transactionRequest.setAmounts(amounts);
        transactionRequest.setStatus(TransactionStatus.PENDING);
        transactionRequest.setSourceAddress(wallet.getAddress());
        transactionRequest.setDestinationAddress(request.getDestinationAddress());
        transactionRequest.setSourceCurrency(CurrencyType.USDC);
        transactionRequest.setDestinationCurrency(CurrencyType.USDC);
        transactionRequest.setDirection(TransactionDirection.DEBIT);
        Transactions transaction = transactionService.createTransactionRecord(transactionRequest);

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setAmount(amounts);
        reservationRequest.setReservationType(ReservationType.OUTBOUND_TRANSFER);
        reservationRequest.setReason(ReservationReason.TRANSACTION_INITIATED);
        Reservation reservation = reservationService.createReservation(wallet, reservationRequest);

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(amounts));
        wallet.setReservedBalance(wallet.getReservedBalance().add(amounts));
        walletRepository.save(wallet);


        try {
            circleWalletService.createTransferIntent(userId, request.getDestinationAddress(),
                            request.getBlockchain(), request.getAmounts(), wallet.getAddress())

                    .doOnSuccess(initiationResponse -> {
                        log.info("payment successfully initiated with response {}, {}", initiationResponse.getId(),
                                initiationResponse.getState());

                        transaction.setProviderTransactionId(initiationResponse.getId());
                        transactionRepository.save(transaction);
                        reservation.setProviderTransactionId(initiationResponse.getId());
                        reservationRepository.save(reservation);

                    })

                    .onErrorResume(error -> {
                        TransferInitiationFailedEvent event = new TransferInitiationFailedEvent(userId,
                                request, reservation.getId(), wallet.getCircleWalletId(), transaction.getId());

                        outboundRetryService.createPaymentRetryRecord(event.getUserId());
                        transferInitiationFailedPublisher.publishTransferInitiationFailedEvent(event);
                        return Mono.error(error);
                    }).subscribe();

        } catch (WebClientRequestException | DnsNameResolverTimeoutException | WebClientResponseException |
                 ExhaustedRetryException e) {
            throw e;

        } catch (Exception e) {
            throw new ExternalServiceException("Error occurred while initiating payment", e);
        }

        TransferResponse response = new TransferResponse();
        response.setMessage("Payment Successfully processed");
        return response;


        /**
         Execution steps:
         validate input
         create txn = pending
         persist intent id
         create hold/reservation
         call provider
         debit source Wallet
         no lock
         no ledger
         no debit
         no release
         */
    }

}
