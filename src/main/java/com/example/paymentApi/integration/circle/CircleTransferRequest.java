package com.example.paymentApi.integration.circle;

public record CircleTransferRequest(
        String idempotencyKey,
        String destinationAddress,
        String[] amounts,
        String feeLevel,
        String entitySecretCiphertext,
        String blockchain,
        String walletAddress

) {
}
