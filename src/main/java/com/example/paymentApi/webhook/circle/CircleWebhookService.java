package com.example.paymentApi.webhook.circle;

import com.example.paymentApi.shared.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class CircleWebhookService {

    private static final String SIGNATURE_ALGORITHM = "ECDSA_SHA_256";
    private static final String KEY_ALGORITHM = "EC";

    @Value("${circle-publicKey-Base64}")
    private static String publicKeyBase64;

    public boolean verifySignature(String rawPayload, String signatureBase64){

        try {

            PublicKey publicKeyBase = loadPublicKeyBase(publicKeyBase64);
            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            byte[] payloadBytes = rawPayload.getBytes(StandardCharsets.UTF_8);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKeyBase);
            signature.update(payloadBytes);

            boolean isVerifiedSignature = signature.verify(signatureBytes);

            if (isVerifiedSignature) {
                return true;
            }
            else {
                throw new ValidationException("Signature is invalid");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static PublicKey loadPublicKeyBase(String publicKeyBase) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(publicKeyBase);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    }
