package com.example.paymentApi.shared.exception;

import com.example.paymentApi.shared.utility.GeneralLogger;
import org.springframework.http.HttpStatus;

public class GeneralAppException extends RuntimeException{
    GeneralLogger logger = new GeneralLogger().getLogger(GeneralAppException.class);

    public GeneralAppException(HttpStatus status, String appErrorCode, String message, String resource) {
        super();
    }
}
