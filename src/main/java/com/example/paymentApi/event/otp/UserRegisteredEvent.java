package com.example.paymentApi.event.otp;

public class UserRegisteredEvent<T>{

    private final String userId;
    private final String email;
    private final T data;

    public T getData() {
        return data;
    }

    public UserRegisteredEvent(String userId, String email, T data){
        this.userId = userId;
        this.email = email;
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

}

