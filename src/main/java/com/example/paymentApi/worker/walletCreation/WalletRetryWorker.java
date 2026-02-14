package com.example.paymentApi.worker.walletCreation;

import com.example.paymentApi.integration.circle.CircleWalletService;
import com.example.paymentApi.event.wallet.WalletCreationEvent;
import com.example.paymentApi.event.wallet.WalletCreationPermanentlyFailedEvent;
import com.example.paymentApi.event.wallet.WalletEventPublisher;
import com.example.paymentApi.shared.enums.RetryStatus;
import com.example.paymentApi.shared.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletRetryWorker {

    private final CircleWalletService circleWalletService;
    private final WalletRetryRepository walletRetryRepository;
    private final WalletEventPublisher walletEventPublisher;
    private static final long MAX_RETRIES = 3;


    public void retryCircleWalletCreation(String userId) {
        List<WalletRetryRecord> records = walletRetryRepository.findByStatus(RetryStatus.PENDING);
        for (WalletRetryRecord record : records) {
            if(record.getStatus() == RetryStatus.SUCCESS) return;
            if(record.getRetryCount() >= MAX_RETRIES) return;
            try {
                circleWalletService.createCircleWallet(record.getUserId())
                        .retryWhen(
                                Retry.backoff(MAX_RETRIES - record.getRetryCount(), Duration.ofSeconds(3))
                                        .filter(this::isRetryable)
                                        .doBeforeRetry(retrySignal -> {
                                            long newCount = retrySignal.totalRetries() + 1;
                                            record.setRetryCount(newCount);
                                            walletRetryRepository.save(record);
                                            log.warn(
                                                    "Retrying Circle wallet creation for user {} - attempt {}",
                                                    record.getUserId(),
                                                    newCount
                                            );
                                        })
                        )

                        .doOnSuccess(response -> {
                            record.setStatus(RetryStatus.SUCCESS);
                            record.setReason("Success");
                            walletRetryRepository.save(record);
                            WalletCreationEvent event = new WalletCreationEvent(response, userId);
                            walletEventPublisher.publishWalletCreatedEvent((event));

                            log.info("Wallet creation successful after retries for user {}", record.getUserId());
                        })

                        .onErrorResume(error -> {
                            record.setStatus(RetryStatus.FAILED);
                            record.setReason(error.getMessage());
                            walletRetryRepository.save(record);
                            walletEventPublisher.publishWalletCreationPermanentlyFailed(new WalletCreationPermanentlyFailedEvent
                                    (userId));

                            log.error(
                                    "Circle wallet creation failed for user {}",
                                    record.getUserId(),
                                    error
                            );

                            return Mono.empty();

                        })
                        .subscribe();

            } catch (Exception e) {
                throw new ExternalServiceException("Error occurred", e);
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
