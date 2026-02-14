package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.wallet.WalletCreationFailedEvent;
import com.example.paymentApi.shared.exception.InternalProcessingException;
import com.example.paymentApi.worker.walletCreation.WalletRetryWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WalletCreationFailedEventListener {

    private final WalletRetryWorker walletRetryWorker;


    @Async
    @EventListener
    public void handleWalletCreationFailedEvent(WalletCreationFailedEvent event){

        try {
            walletRetryWorker.retryCircleWalletCreation(event.getUserId());

        }
        catch (Exception e) {
            throw new InternalProcessingException("Error occurred while processing retries", e);

        }

    }
}
