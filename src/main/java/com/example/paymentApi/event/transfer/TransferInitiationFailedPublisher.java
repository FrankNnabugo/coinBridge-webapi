package com.example.paymentApi.event.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferInitiationFailedPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishTransferInitiationFailedEvent(TransferInitiationFailedEvent event){
    publisher.publishEvent(event);
    }

    public void publishTransferInitiationPermanentlyFailedEvent(TransferInitiationPermanentlyFailedEvent event){
        publisher.publishEvent(event);

    }

}
