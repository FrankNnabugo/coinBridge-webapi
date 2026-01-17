package com.example.paymentApi.event.listeners;

import com.example.paymentApi.event.wallet.WalletCreationPermanentlyFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class WalletCreationPermanentlyFailedEventListener {

    @Async
    @EventListener
    public void handleWalletCreationPermanentlyFailedEvent(WalletCreationPermanentlyFailedEvent event){

        //TODO:
        //Call recovery method or notify user via email notification

        log.error(
                "Circle wallet creation permanently failed for user {}",
                event.getUserId()
        );
    }
}
