package com.example.paymentApi.shared.utility;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class PasswordHashUtil {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private final GeneralLogger logger;

    public PasswordHashUtil(GeneralLogger logger){
        this.logger = logger;
    }
    public String hashPassword(String password){
        try {
            byte[] salt = generateSalt();
            byte[] hash = pbkdf2(password.toCharArray(), salt);

            return Base64.getEncoder().encodeToString(salt) + ":" +
                    Base64.getEncoder().encodeToString(hash);
        }
        catch(Exception exception){
            logger.log(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    public boolean verifyPassword(String password, String stored){
        try {
            String[] parts = stored.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            byte[] newHash = pbkdf2(password.toCharArray(), salt);

            return slowEquals(storedHash, newHash);
        }
        catch(Exception exception){
            logger.log(exception.getMessage());

            throw new RuntimeException(exception);
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
