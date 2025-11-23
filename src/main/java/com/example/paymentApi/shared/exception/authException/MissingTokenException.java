package com.example.paymentApi.shared.exception.authException;

public class MissingTokenException extends RuntimeException{
    public MissingTokenException(String message) {
        super(message);
    }
}
