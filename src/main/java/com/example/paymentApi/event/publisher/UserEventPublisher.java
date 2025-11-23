package com.example.paymentApi.event.publisher;

import com.example.paymentApi.event.model.UserCreatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher{

    private final ApplicationEventPublisher publisher;

    public UserEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishUserCreated(String userId, String email) {
        publisher.publishEvent(new UserCreatedEvent(userId, email));
    }

}
