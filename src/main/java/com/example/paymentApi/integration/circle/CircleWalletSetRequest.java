package com.example.paymentApi.integration.circle;

public record CircleWalletSetRequest(
        String idempotencyKey,
        String name,
        String entitySecretCiphertext
) {
}
