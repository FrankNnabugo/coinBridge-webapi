package com.example.paymentApi.worker.paymentInitiation;

import com.example.paymentApi.event.transfer.TransferInitiationFailedPublisher;
import com.example.paymentApi.event.transfer.TransferInitiationPermanentlyFailedEvent;
import com.example.paymentApi.integration.circle.CircleWalletService;
import com.example.paymentApi.shared.enums.RetryStatus;
import com.example.paymentApi.shared.exception.ExternalServiceException;
import com.example.paymentApi.walletTransaction.TransferRequest;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.resolver.dns.DnsNameResolverTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboundRetryWorker {

    private final CircleWalletService circleWalletService;
    private final OutboundRetryRepository outboundRetryRepository;
    private static final long MAX_RETRIES = 3;
    private final WalletRepository walletRepository;
    private final TransferInitiationFailedPublisher transferInitiationFailedPublisher;

    public void retryPaymentInitiation(TransferRequest transferRequest, String userId,
                                       String reservationId,
                                       String transactionId){

        Wallet wallet = walletRepository.findByUser_id(userId);
        List<OutboundRetryRecord> records = outboundRetryRepository.findByRetryStatus(RetryStatus.PENDING);
        for (OutboundRetryRecord record : records) {
            if(record.getRetryStatus() == RetryStatus.SUCCESS) return;
            if(record.getRetryCount() >= MAX_RETRIES) return;

            try {
                circleWalletService.createTransferIntent(record.getUserId(), transferRequest.getDestinationAddress(),
                                transferRequest.getBlockchain(),
                                (transferRequest.getAmounts()), wallet.getAddress())

                        .retryWhen(
                                Retry.backoff(MAX_RETRIES - record.getRetryCount(), Duration.ofSeconds(3))
                                        .filter(this::isRetryable)
                                        .doBeforeRetry(retrySignal -> {
                                            long newCount = retrySignal.totalRetries() + 1;
                                            record.setRetryCount(newCount);
                                            outboundRetryRepository.save(record);

                                            log.warn("retrying payment initiation for user {} - attempt {}", record.getUserId(), newCount
                                            );
                                        })
                        )
                        .doOnSuccess(initiationResponse -> {
                            record.setRetryStatus(RetryStatus.SUCCESS);
                            record.setReason(initiationResponse.getState());
                            outboundRetryRepository.save(record);

                            log.info("Payment initiation successful after retries for user {} - attempt {}", record.getUserId(), record.getRetryCount());

                        })
                        .onErrorResume(error -> {
                            record.setRetryStatus(RetryStatus.FAILED);
                            record.setReason(error.getMessage());
                            outboundRetryRepository.save(record);

                            log.info("Payment permanently failed after retries, publishing event");
                            TransferInitiationPermanentlyFailedEvent event = new TransferInitiationPermanentlyFailedEvent(userId,
                                    transferRequest.getAmounts(), reservationId, wallet.getCircleWalletId(), transactionId);
                            transferInitiationFailedPublisher.publishTransferInitiationPermanentlyFailedEvent(event);
                            return Mono.error(error);

                        })
                        .subscribe();

            } catch (Exception e) {
                throw new ExternalServiceException("Circle payment initiation api call failed", e);
            }
        }
    }

    private boolean isRetryable(Throwable throwable) {

        if (throwable instanceof WebClientResponseException ex) {
            int status = ex.getStatusCode().value();

            return status == 500
                    || status == 502
                    || status == 503
                    || status == 504;
        }

        return throwable instanceof WebClientRequestException;

    }

}
