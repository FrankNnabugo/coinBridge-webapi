package com.example.paymentApi.webhook.circle;

import com.example.paymentApi.shared.exception.IllegalStateException;
import com.example.paymentApi.shared.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class CircleWebhookService {

    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";
    private static final String KEY_ALGORITHM = "EC";

    @Value("${circle-publicKey-Base64}")
    private String publicKeyBase64;

    public boolean verifySignature(String rawPayload, String signatureBase64){

        try {

            PublicKey publicKeyBase = loadPublicKeyBase(publicKeyBase64);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            byte[] payloadBytes = rawPayload.getBytes(StandardCharsets.UTF_8);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKeyBase);
            signature.update(payloadBytes);

            return signature.verify(signatureBytes);


        } catch (Exception e) {
            log.error("Circle signature verification failed", e);
            return false;
        }

    }

    private static PublicKey loadPublicKeyBase(String publicKeyBase) {

        try {

            byte[] decodedKey = Base64.getDecoder().decode(publicKeyBase);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to load Circle public key");
        }
    }
    }
