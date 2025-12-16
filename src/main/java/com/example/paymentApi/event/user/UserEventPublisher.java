package com.example.paymentApi.event.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {

    private final ApplicationEventPublisher publisher;

    public UserEventPublisher(ApplicationEventPublisher publisher){
        this.publisher = publisher;
    }

    public void publishUserCreatedEvent(String userId){
        publisher.publishEvent(new UserCreationEvent<>(userId));
    }
}
