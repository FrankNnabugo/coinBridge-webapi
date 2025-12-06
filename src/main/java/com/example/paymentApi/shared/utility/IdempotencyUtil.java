package com.example.paymentApi.shared.utility;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class IdempotencyUtil {

    private final StringRedisTemplate redisTemplate;
    private static final long TTL_HOURS = 2;

    public IdempotencyUtil(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;

    }

    public String getOrCreateKey(String userId) {

        String redisKey = "idempotency:wallet:" + userId;

        String existingKey = redisTemplate.opsForValue().get(redisKey);
        if (existingKey != null) {
            return existingKey;
        }

        String newKey = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(redisKey, newKey, TTL_HOURS, TimeUnit.HOURS);

        return newKey;
    }

}
