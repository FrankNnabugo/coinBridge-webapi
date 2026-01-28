package com.example.paymentApi.shared.utility;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class EntitySecretCipherTextUtil{

    /**
     * Convert Circle's public key string (Base64) into a PublicKey object
     */
    public static PublicKey loadCirclePublicKey(String base64PublicKey) throws Exception {

        byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    /**
     * Encrypt the 32-byte entity secret using RSA-OAEP-SHA256
     */
    public static String encryptEntitySecret(PublicKey publicKey, byte[] entitySecret) throws Exception {
        if (entitySecret.length != 32) {
            throw new IllegalArgumentException("Entity secret must be exactly 32 bytes");
        }

        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);

        byte[] encrypted = cipher.doFinal(entitySecret);
        return Base64.getEncoder().encodeToString(encrypted);
    }


    /**
     * Helper to convert Base64 32-byte secret string to raw bytes
     */
    public static byte[] decodeEntitySecret(String base64Secret) {
        return Base64.getDecoder().decode(base64Secret);
    }

}
