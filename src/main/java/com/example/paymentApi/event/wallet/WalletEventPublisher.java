package com.example.paymentApi.event.wallet;

import com.example.paymentApi.integration.CircleWalletResponse;
import org.springframework.context.ApplicationEventPublisher;

public class WalletEventPublisher {
    private final ApplicationEventPublisher publisher;

    public WalletEventPublisher(ApplicationEventPublisher publisher){
        this.publisher = publisher;
    }

    public void publishWalletCreatedEvent(CircleWalletResponse circleWalletResponse, String userId){
        publisher.publishEvent(new WalletCreatedEvent(circleWalletResponse, userId));
    }

}
