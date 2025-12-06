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
    @Value("${32-byte-secret-key}")
    private String entitySecret;

    @Value("${public-key}")
    private String circlePublicKey;

    @Bean
    CommandLineRunner testRedis(RedisTemplate<String, String> redisTemplate) {
        return args -> {
            try {
                redisTemplate.opsForValue().set("test-key", "hello");
                String value = redisTemplate.opsForValue().get("test-key");
                System.out.println("Redis Test Value: " + value);
            } catch (Exception e) {
                System.out.println("Redis Error: " + e.getMessage());
            }
        };
    }

//    @Bean
//    CommandLineRunner testCiphertext(EntitySecretCipherTextUtil entitySecretCipherTextUtil) {
//
//        return args -> {
//
//            try {
//
//                String cleanedKey = PublicKeyFormatter.formatPublicKey(circlePublicKey);
//                PublicKey publicKey = entitySecretCipherTextUtil.loadCirclePublicKey(cleanedKey);
//                byte[] entitySecretBytes = entitySecretCipherTextUtil.decodeEntitySecret(entitySecret);
//                String entitySecretCipherText = entitySecretCipherTextUtil.encryptEntitySecret(publicKey, entitySecretBytes);
//
//                System.out.println("Ciphertext length: " + entitySecretCipherText.length());
//                System.out.println("Ciphertext: " + entitySecretCipherText);
//
//            } catch (Exception e) {
//                System.out.println("Ciphertext Error: " + e.getMessage());
//
//            }
//        };
//    }
}



