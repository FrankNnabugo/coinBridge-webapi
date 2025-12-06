package com.example.paymentApi.integration;

public record CircleWalletSetRequest(
        String idempotencyKey,
        String name,
        String entitySecretCiphertext
) {
}
