package com.example.paymentApi.shared.utility;

import com.example.paymentApi.shared.enums.TransferBlockchain;
import com.example.paymentApi.shared.exception.IllegalArgumentException;
import com.example.paymentApi.shared.exception.NullParameterException;
import com.example.paymentApi.shared.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Verifier {

    private String url;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern OTP_PATTERN = Pattern.compile("^\\d{5}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{6,}$");


    private static final Pattern EVM_ADDRESS_PATTERN =
            Pattern.compile("^0x[a-fA-F0-9]{40}$");


    public Verifier setResourceUrl(String url) {
        this.url = url;
        return this;
    }

    public void verifyParams(String... params){
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                throw new NullParameterException("Please provide all the required information");
            }
        }
    }


    public static void verifyObject(Object... objects){
        for (Object object : objects) {
            if (object == null) {
               throw new NullParameterException("Please provide all the required information");
            }
        }
    }


    public static void verifyEmail(String param){
        if (param == null || param.isEmpty()) {
           throw new NullParameterException("email cannot be empty");
        }

        if (!patternMatches(param)) {
            throw new IllegalArgumentException("Invalid email format provided");
        }

    }

    private static boolean patternMatches(String emailAddress){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
        return matcher.matches();
    }


    public static void verifyOtpFormat(String otp){
        if (otp == null || otp.isEmpty()) {
            throw new NullParameterException("Otp cannot be empty");
        }
        if (!OTP_PATTERN.matcher(otp).matches()) {
            throw new IllegalArgumentException("Invalid Otp format, please provide a valid Otp");
        }
    }


    public static void verifyPasswordFormat(String password){
        if (password == null || password.isEmpty()) {
            throw new NullParameterException("Password cannot be empty");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must contain at least 1 uppercase letter, " +
                    "lowercase letter, number, and special character, and must be at least 6 characters long");
        }
    }

    public static void validatePaymentInput(
            String address,
            TransferBlockchain blockchain) {

        if (!EVM_ADDRESS_PATTERN.matcher(address).matches()) {
            throw new ValidationException("Invalid EVM wallet address");
        }

        if (blockchain!= TransferBlockchain.MATIC_AMOY) {
            throw new ValidationException(
                    "Unsupported blockchain. Only MATIC is allowed"
            );
        }
    }

    public static void validateAllInput(String address, TransferBlockchain blockchain, String[] amounts){
        if (amounts == null
                && address == null
                && blockchain == null) {
            throw new ValidationException("Amount, address, blockchain must be provided");
        }
    }
}
