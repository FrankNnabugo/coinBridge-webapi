package com.example.paymentApi.integration;

public record CircleWalletRequest(
        String idempotencyKey,
        String accountType,
        String[] blockchains,
        int count,
        String entitySecretCiphertext,
        String walletSetId

) {

}
