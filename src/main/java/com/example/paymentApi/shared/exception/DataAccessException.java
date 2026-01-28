package com.example.paymentApi.shared.exception;

public class DataAccessException extends RuntimeException{
    public DataAccessException(String message){
        super(message);
    }
}
