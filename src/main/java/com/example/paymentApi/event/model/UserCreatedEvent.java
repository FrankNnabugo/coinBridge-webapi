package com.example.paymentApi.event.model;

public class UserCreatedEvent{

    private final String userId;
    private final String email;

    public UserCreatedEvent(String userId, String email){
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

}

