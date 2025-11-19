package com.example.paymentApi.shared.utility;

import com.example.paymentApi.shared.ExceptionThrower;
import com.example.paymentApi.shared.exception.GeneralAppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Verifier {
    private final ExceptionThrower exceptionThrower;
    private String resourceUrl;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern OTP_PATTERN = Pattern.compile("^\\d{5}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{6,}$");


    @Autowired
    public Verifier(ExceptionThrower exceptionThrower) {
        this.exceptionThrower = exceptionThrower;
    }

    public Verifier setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
        return this;
    }

    public void verifyParams(String... params) throws GeneralAppException {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                exceptionThrower.throwNullParameterException(resourceUrl);
            }
        }
    }

    public void isValidString(String param){
        if(param != null && !param.isEmpty()){
            exceptionThrower.throwNullParameterException(resourceUrl);
        };
    }

    public void verifyObject(Object... objects) throws GeneralAppException {
        for (Object object : objects) {
            if (object == null) {
                exceptionThrower.throwNullParameterException(resourceUrl);
            }
        }
    }

    public void verifyInteger(String... params) throws GeneralAppException {
        for (String param : params) {
            try {
                Integer.parseInt(param);
            } catch (Exception e) {
                exceptionThrower.throwInvalidIntegerAttributeException(resourceUrl);
            }
        }
    }

    public void verifyIntGreaterThanZero(int param) throws GeneralAppException {
        if (param < 1) exceptionThrower.throwInvalidIntegerAttributeException(resourceUrl);
    }

    public void verifyEmail(String param) throws GeneralAppException {
        if (param == null || param.isEmpty()) {
            exceptionThrower.throwNullParameterException(resourceUrl);
        }

        if (!patternMatches(param)) {
            exceptionThrower.throwInvalidEmailAttributeException(resourceUrl);
        }

    }

    private static boolean patternMatches(String emailAddress) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
        return matcher.matches();
    }


    public void verifyOtpFormat(String resourceUrl, String otp) throws GeneralAppException {
        if (otp == null || otp.isEmpty()) {
            exceptionThrower.throwNullParameterException(resourceUrl);
        }
        if (otp != null && !OTP_PATTERN.matcher(otp).matches()) {
            exceptionThrower.throwInvalidIntegerAttributeException(resourceUrl);
        }
    }


    public void verifyPasswordFormat(String resourceUrl, String password) throws GeneralAppException {
        if (password == null || password.isEmpty()) {
            exceptionThrower.throwNullParameterException(resourceUrl);
        }

        if (password != null && !PASSWORD_PATTERN.matcher(password).matches()) {
            exceptionThrower.throwInvalidPasswordAttributeException(resourceUrl);
        }
    }

}
