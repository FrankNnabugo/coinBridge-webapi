package com.example.paymentApi.shared.exception;

import com.example.paymentApi.shared.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DuplicateRecordException.class)
    public ResponseEntity<ApiError> handleDuplicateRecordException(DuplicateRecordException e, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "DUPLICATE_RECORD_ERROR",
                e.getMessage(),
                HttpRequestUtil.getServletPath()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException e, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.UNAUTHORIZED.value(),
                "VALIDATION_ERROR",
                e.getMessage(),
                HttpRequestUtil.getServletPath()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiError error = new ApiError(
               HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
               "RESOURCE_NOT_FOUND_ERROR",
                HttpRequestUtil.getServletPath()
        );
       return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request){
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                "ILLEGAL_ARGUMENT_ERROR",
                HttpRequestUtil.getServletPath()
                );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalStateException(IllegalStateException e){
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                "ILLEGAL_STATE_ERROR",
                HttpRequestUtil.getServletPath()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(NullParameterException.class)
    public ResponseEntity<ApiError> handleNullParameterException(NullParameterException e){
        ApiError error = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                "NULL_PARAMETER_EXCEPTION",
                HttpRequestUtil.getServletPath()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GeneralAppException.class)
    public ResponseEntity<ApiError> handleGeneralAppException(GeneralAppException e, HttpServletRequest request){
        ApiError error = new ApiError(
                e.getStatus().value(),
                e.getErrorCode(),
                e.getMessage(),
                HttpRequestUtil.getServletPath()
        );
        return new ResponseEntity<>(error, e.getStatus());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericExceptions(Exception e, HttpServletRequest request) {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                e.getMessage(),
                HttpRequestUtil.getServletPath()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
