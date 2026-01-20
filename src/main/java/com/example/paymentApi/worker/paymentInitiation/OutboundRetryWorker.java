package com.example.paymentApi.worker.paymentInitiation;

import com.example.paymentApi.integration.circle.CircleWalletService;
import com.example.paymentApi.shared.enums.RetryStatus;
import com.example.paymentApi.walletToWallet.outbound.OutBoundRequest;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.webhook.circle.OutboundTransferInitiationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

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

    public void retryPaymentInitiation(OutBoundRequest outBoundRequest, String userId){

        Wallet wallet = walletRepository.findByUser_id(userId);
        List<OutboundRetryRecord> records = outboundRetryRepository.findByRetryStatus(RetryStatus.PENDING);
        for(OutboundRetryRecord record: records){
           try{
               circleWalletService.createTransferIntent(record.getUserId(), outBoundRequest.getDestinationAddress(),
                       outBoundRequest.getBlockchain(),
                               (outBoundRequest.getAmounts()), wallet.getAddress())

                       .retryWhen(
                               Retry.backoff(MAX_RETRIES, Duration.ofSeconds(3))
                                       .doBeforeRetry(retrySignal -> {
                                           long newCount = retrySignal.totalRetries() + 1;
                                           record.setRetryCount(newCount);
                                           outboundRetryRepository.save(record);

                                           log.warn("retrying payment initiation for user {} - attempt {}", record.getUserId(), newCount
                                           );
                                       })
                       )
                       .doOnSuccess(OutboundTransferInitiationResponse->{
                           OutboundTransferInitiationResponse outBound = new OutboundTransferInitiationResponse();
                           record.setRetryStatus(RetryStatus.SUCCESS);
                           record.setReason(outBound.getState());
                           outboundRetryRepository.save(record);

                           log.info("Payment initiation successful after retries for user {}", record.getUserId());

                       })
                       .onErrorResume(error->{
                         record.setRetryStatus(RetryStatus.FAILED);
                         record.setReason(error.getMessage());
                         outboundRetryRepository.save(record);

                         /**
                         publish transfer permanently failed event
                          Listener calls for hold release and let transaction and reservation reflect this event
                          update reservation status to release and reason to transaction_failed
                          update transaction record to failed
                          */

                           log.error(
                                   "payment initiation failed for user {}",
                                   record.getUserId(),
                                   error
                           );

                           return Mono.empty();

                       })
                       .subscribe();

           } catch (Exception e) {
               throw new RuntimeException(e);
           }
        }
    }

}
