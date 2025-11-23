package com.example.paymentApi.shared.exception;

import com.example.paymentApi.shared.utility.GeneralLogger;
import org.springframework.http.HttpStatus;

public class GeneralAppException extends RuntimeException{
    GeneralLogger logger = new GeneralLogger().getLogger(GeneralAppException.class);

        private final HttpStatus status;
        private final String errorCode;
        private final String message;
        private final String path;

        public GeneralAppException(HttpStatus status, String errorCode, String message, String path) {
            super(message);
            this.status = status;
            this.errorCode = errorCode;
            this.message = message;
            this.path = path;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public String getErrorCode() {
            return errorCode;
        }

        @Override
        public String getMessage() {
            return message;
        }

        public String getPath() {
            return path;
        }

}
