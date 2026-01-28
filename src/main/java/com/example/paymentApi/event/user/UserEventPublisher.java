package com.example.paymentApi.event.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishUserCreatedEvent(String userId){
        publisher.publishEvent(new UserCreationEvent<>(userId));
    }
}
