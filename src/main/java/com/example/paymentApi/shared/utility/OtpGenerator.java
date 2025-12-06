package com.example.paymentApi.shared.utility;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class OtpGenerator{

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int EXPIRY_MINUTES = 5;

    private OtpGenerator() {
    }

    /**
     * Generates a new OTP and its expiry time.
     *
     * @return OtpData object containing the OTP and expiry time
     */
    public static OtpData generateOtp() {
        int otpValue = secureRandom.nextInt(100_000); // from 00000 to 99999
        String otp = String.format("%05d", otpValue);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(EXPIRY_MINUTES);

        return new OtpData(otp, expiryTime);
    }

    /**
     * Simple holder for OTP and expiry time.
     */
    public static class OtpData {
        private final String otp;
        private final LocalDateTime expiryTime;

        public OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }
    }

     public static String getExpiryDurationString(LocalDateTime expiryTime) {
        long minutes = Duration.between(LocalDateTime.now(), expiryTime).toMinutes();
        return minutes + " minutes";
    }

}
