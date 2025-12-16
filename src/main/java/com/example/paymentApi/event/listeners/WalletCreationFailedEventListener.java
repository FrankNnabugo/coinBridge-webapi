package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.wallet.WalletCreationFailedEvent;
import com.example.paymentApi.worker.WalletRetryService;
import com.example.paymentApi.worker.WalletRetryWorker;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WalletCreationFailedEventListener {

    private final WalletRetryService walletRetryService;
    private final WalletRetryWorker walletRetryWorker;

     public WalletCreationFailedEventListener(WalletRetryService walletRetryService, WalletRetryWorker walletRetryWorker){
         this.walletRetryService = walletRetryService;
         this.walletRetryWorker = walletRetryWorker;
     }


    @Async
    @EventListener
    public void handleWalletCreationFailedEvent(WalletCreationFailedEvent event){

        try {
            walletRetryService.createRetryRecord(event.getUserId());
            walletRetryWorker.retryCircleWalletCreation(event.getUserId());

        }
        catch (Exception e) {

            throw new RuntimeException(e.getMessage());

        }

    }
}
