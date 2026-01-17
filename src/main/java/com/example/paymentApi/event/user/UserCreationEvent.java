package com.example.paymentApi.event.user;

public class UserCreationEvent<T> {

    private final String userId;

    private final String email;

    public UserCreationEvent(String userId, String email){
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
