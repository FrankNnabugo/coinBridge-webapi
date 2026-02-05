package com.example.paymentApi.shared.utility;

import com.example.paymentApi.wallets.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;
    private static final long WALLET_TTL = 2;
    private static final long PAYMENT_LOCK_TTL = 2;


    public String getOrCreateKey(String userId) {

        String redisKey = "idempotency:wallet:" + userId;

        String existingKey = redisTemplate.opsForValue().get(redisKey);
        if (existingKey != null && WALLET_TTL != 0 && WALLET_TTL > 0) {
            return existingKey;
        }

        String newKey = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(redisKey, newKey, WALLET_TTL, TimeUnit.MINUTES);
        return newKey;

    }

    public boolean acquireLock(String circleWalletId) {

        String redisKey = "payment:lock:" + circleWalletId;
        String newKey = UUID.randomUUID().toString();
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue()
                        .setIfAbsent(redisKey, newKey, PAYMENT_LOCK_TTL, TimeUnit.MINUTES)
        );
    }

    public void releaseLock(String key ){

        Boolean deleted = redisTemplate.delete(key);
    }

}
