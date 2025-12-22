package com.example.paymentApi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class CommandLineConfig {

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



