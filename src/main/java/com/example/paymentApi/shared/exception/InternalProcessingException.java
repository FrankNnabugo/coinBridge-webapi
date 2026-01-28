package com.example.paymentApi.shared.exception;

public class InternalProcessingException extends RuntimeException{
    public InternalProcessingException(String message, Exception exception){
        super(message);
    }
}
