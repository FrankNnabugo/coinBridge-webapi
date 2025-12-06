package com.example.paymentApi.shared.utility;

import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
public class PublicKeyFormatter {
    public static String formatPublicKey(String key){
        return key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "")
                .replaceAll("[^A-Za-z0-9+/=]", ""); // removes \n, \r, spaces
    }
}
