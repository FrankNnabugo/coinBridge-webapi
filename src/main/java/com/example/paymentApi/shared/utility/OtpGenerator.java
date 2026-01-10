package com.example.paymentApi.shared.utility;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class OtpGenerator{

    private static final SecureRandom secureRandom = new SecureRandom();

    private OtpGenerator() {
    }

    public static OtpData generateOtp() {
        int otpValue = secureRandom.nextInt(100_000); // from 00000 to 99999
        String otp = String.format("%05d", otpValue);

        return new OtpData(otp);
    }


    public static class OtpData {
        private final String otp;

        public OtpData(String otp) {
            this.otp = otp;
        }

        public String getOtp() {
            return otp;
        }

    }

     public static String getExpiryDurationString(LocalDateTime expiryTime) {
        long minutes = Duration.between(LocalDateTime.now(), expiryTime).toMinutes();
        return minutes + " minutes";
    }

}
