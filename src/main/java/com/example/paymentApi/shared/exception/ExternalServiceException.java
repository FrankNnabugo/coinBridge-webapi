package com.example.paymentApi.shared.exception;

public class ExternalServiceException extends RuntimeException{
    public ExternalServiceException(String message, Exception exception){
        super(message);
    }
}
