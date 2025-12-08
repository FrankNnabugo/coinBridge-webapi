package com.example.paymentApi.event.user;

public class UserCreatedEvent<T> {

    private final String userId;

    public UserCreatedEvent(String userId){
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
