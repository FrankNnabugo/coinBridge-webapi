package com.example.paymentApi.event.computeLedgerBalance;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LedgerEntryPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishLedgerEntryCreatedEvent(LedgerEntryEvent event){
        publisher.publishEvent(event);

    }
}
