package com.example.paymentApi.event.wallet;

import com.example.paymentApi.integration.CircleWalletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class WalletEventPublisher {
    private final ApplicationEventPublisher publisher;

    public WalletEventPublisher(ApplicationEventPublisher publisher){
        this.publisher = publisher;
    }

    public void publishWalletCreatedEvent(WalletCreatedEvent event){
        publisher.publishEvent(event);
    }

}
