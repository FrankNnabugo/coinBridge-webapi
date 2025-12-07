package com.example.paymentApi.event.otp;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventPublisher {

    private final ApplicationEventPublisher publisher;

    public UserRegisteredEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishUserCreated(String userId, String email, String data) {
        publisher.publishEvent(new UserRegisteredEvent<String>(userId, email, data));
    }

}
