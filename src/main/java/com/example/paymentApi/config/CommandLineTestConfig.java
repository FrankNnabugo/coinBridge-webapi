package com.example.paymentApi.config;

import com.example.paymentApi.shared.exception.IllegalArgumentException;
import com.example.paymentApi.shared.utility.EntitySecretCipherTextUtil;
import com.example.paymentApi.shared.utility.PublicKeyFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.security.PublicKey;

@Configuration
public class CommandLineTestConfig {

    @Bean
    CommandLineRunner testRedis(RedisTemplate<String, String> redisTemplate) {
        return args -> {

            try {
                redisTemplate.opsForValue().set("test-key", "hello");
                String value = redisTemplate.opsForValue().get("test-key");
                System.out.println("Redis Test Value: " + value);
            }
            catch (Exception e)
            {
                System.out.println("Redis Error: " + e.getMessage());
            }
        };
    }
}



