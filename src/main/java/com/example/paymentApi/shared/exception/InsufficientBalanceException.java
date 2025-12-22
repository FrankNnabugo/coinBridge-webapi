package com.example.paymentApi.shared.exception;

import jakarta.persistence.criteria.CriteriaBuilder;

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException(String message){
        super(message);
    }
}
