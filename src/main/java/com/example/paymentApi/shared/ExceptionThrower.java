package com.example.paymentApi.shared;

import com.example.paymentApi.shared.exception.GeneralAppException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ExceptionThrower {

    //1000 series - general
    //2000 series - input/parameter related
    //3000 series - user entity related
    //4000 series - all other business related

    //General
    private static final String INVALID_TOKEN_ERROR_CODE = "1000";
    private static final String INVALID_TOKEN_ERROR = "Invalid or expired token supplied";

    private static final String TERMS_NOT_ACCEPTED_ERROR_CODE = "1001";
    private static final String TERMS_NOT_ACCEPTED_ERROR = "You must accept the terms and conditions to create an account.";

    //Input/parameter
    private final String NULL_PARAMETER_ERROR_CODE = "2000";
    private final String NULL_PARAMETER_ERROR = "Please provide all the required information";
    private final String INVALID_EMAIL_PARAMETER_ERROR_CODE = "2001";
    private final String INVALID_EMAIL_PARAMETER_ERROR = "Invalid email format provided";
    private final String INVALID_INTEGER_PARAMETER_ERROR_CODE = "2002";
    private final String INVALID_INTEGER_PARAMETER_ERROR = "A number is expected, please provided a valid number";

    //User
    private final String USER_ALREADY_EXIST_ERROR_CODE = "3000";
    private final String USER_ALREADY_EXIST_ERROR = "User already exist";
    private final String USER_DOES_NOT_EXIST_ERROR_CODE = "3001";
    private final String USER_DOES_NOT_EXIST_ERROR = "User not found";
    private final String INVALID_LOGIN_CREDENTIALS_ERROR_CODE = "3002";
    private final String INVALID_LOGIN_CREDENTIALS_ERROR = "Invalid Login Credential.";
    private final String USER_NOT_VERIFIED_ERROR_CODE = "3003";
    private final String USER_NOT_VERIFIED_ERROR = "Please verify your account to enable login";
    private final String OTP_NOT_FOUND_ERROR_CODE = "3004";
    private final String OTP_NOT_FOUND_ERROR = "No OTP found. Please request otp.";
    private final String OTP_EXPIRED_ERROR_CODE = "3005";
    private final String OTP_EXPIRED_ERROR = "OTP has expired. Please request a new one.";
    private final String INVALID_OTP_ERROR_CODE = "3006";
    private final String INVALID_OTP_ERROR = "Invalid OTP. Please try again.";
    private final String INVALID_REFRESH_TOKEN_ERROR_CODE = "3007";
    private final String INVALID_REFRESH_TOKEN_ERROR = "Invalid or expired refreshToken";
    private final String INVALID_PASSWORD_ERROR_CODE = "3008";
    private final String INVALID_PASSWORD_ERROR = "Password must contain at least 1 uppercase letter, " +
            "lowercase letter, number, and special character, and must be at least 6 characters long";




    public void throwInvalidTokenException(String path){
        throw new GeneralAppException(HttpStatus.UNAUTHORIZED,
                INVALID_TOKEN_ERROR_CODE,
                INVALID_TOKEN_ERROR,
                path);
    }

    public void throwNullParameterException(String path){
        throw new GeneralAppException(HttpStatus.UNAUTHORIZED,
                NULL_PARAMETER_ERROR_CODE,
                NULL_PARAMETER_ERROR,
                path);
    }

    public void throwInvalidEmailAttributeException(String path){
        throw new GeneralAppException(HttpStatus.BAD_REQUEST,
                INVALID_EMAIL_PARAMETER_ERROR_CODE,
                INVALID_EMAIL_PARAMETER_ERROR,
                path);
    }


    public void throwInvalidIntegerAttributeException(String path){
        throw new GeneralAppException(HttpStatus.BAD_REQUEST,
                INVALID_INTEGER_PARAMETER_ERROR_CODE,
                INVALID_INTEGER_PARAMETER_ERROR,
                path);
    }

    public void throwUserAlreadyExistException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                USER_ALREADY_EXIST_ERROR_CODE,
                USER_ALREADY_EXIST_ERROR,
                path
        );
    }
    public GeneralAppException throwUserNotFoundExistException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                USER_DOES_NOT_EXIST_ERROR_CODE,
                USER_DOES_NOT_EXIST_ERROR,
                path
        );
    }

    public void throwInvalidLoginException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                INVALID_LOGIN_CREDENTIALS_ERROR_CODE,
                INVALID_LOGIN_CREDENTIALS_ERROR,
                path
        );
    }

    public void throwUserNotVerifiedException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                USER_NOT_VERIFIED_ERROR_CODE,
                USER_NOT_VERIFIED_ERROR,
                path
        );
    }

    public void throwOtpNotFoundException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                OTP_NOT_FOUND_ERROR_CODE,
                OTP_NOT_FOUND_ERROR,
                path
        );
    }

    public void throwOtpExpiredException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                OTP_EXPIRED_ERROR_CODE,
                OTP_EXPIRED_ERROR,
                path
        );

    }

    public void throwInvalidOtpException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                INVALID_OTP_ERROR_CODE,
                INVALID_OTP_ERROR,
                path
        );

    }

    public void throwInvalidRefreshTokenException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                INVALID_REFRESH_TOKEN_ERROR_CODE,
                INVALID_REFRESH_TOKEN_ERROR,
                path
        );
    }

    public void throwInvalidPasswordAttributeException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                INVALID_PASSWORD_ERROR_CODE,
                INVALID_PASSWORD_ERROR,
                path
        );
    }

    public void throwTermsNotAcceptedException(String path){
        throw new GeneralAppException(
                HttpStatus.BAD_REQUEST,
                TERMS_NOT_ACCEPTED_ERROR_CODE,
                TERMS_NOT_ACCEPTED_ERROR,
                path
        );
    }
}
