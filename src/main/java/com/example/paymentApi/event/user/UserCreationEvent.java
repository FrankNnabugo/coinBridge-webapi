package com.example.paymentApi.event.user;

public class UserCreationEvent<T> {
    private final String userId;

    public UserCreationEvent(String userId){
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}
