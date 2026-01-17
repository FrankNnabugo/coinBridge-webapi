package com.example.paymentApi.users;

import com.example.paymentApi.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisOtpService {

    private final StringRedisTemplate redisTemplate;


    private String buildKey(String userId, String purpose) {
        return "otp:user:" + userId + ":" + purpose;
    }

    public void saveOtp(String userId, String purpose, String otp, long TTL){

        String key = buildKey(userId, purpose);

        redisTemplate.opsForValue().set(key, otp, TTL, TimeUnit.MINUTES);

    }


    public void verifyOtp(String userId, String purpose, String otp, long TTL){

        String key = buildKey(userId, purpose);

        String savedOtp = redisTemplate.opsForValue().get(key);

        if (savedOtp == null || TTL < 0) {
           throw new ValidationException("OTP has expired. Please request a new one");
        }

        if (!savedOtp.equals(otp)) {
           throw new ValidationException("Invalid OTP. Please request a new Otp");
        }

        redisTemplate.delete(key);

    }
}
