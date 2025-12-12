package com.example.paymentApi.circle;

public record CircleWalletSetRequest(
        String idempotencyKey,
        String name,
        String entitySecretCiphertext
) {
}
