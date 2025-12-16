package com.example.paymentApi.event.wallet;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class WalletEventPublisher {
    private final ApplicationEventPublisher publisher;

    public WalletEventPublisher(ApplicationEventPublisher publisher){
        this.publisher = publisher;
    }

    public void publishWalletCreatedEvent(WalletCreationEvent event){
        publisher.publishEvent(event);
    }

    public void publishWalletCreationFailed(WalletCreationFailedEvent event){
        publisher.publishEvent(event);
    }

    public void publishWalletCreationPermanentlyFailed(WalletCreationPermanentlyFailedEvent event){
        publisher.publishEvent(event);
    }
}
