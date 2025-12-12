package com.example.paymentApi.shared.exception;

public class NullParameterException extends RuntimeException{
    public NullParameterException(String message){
        super(message);
    }
}
