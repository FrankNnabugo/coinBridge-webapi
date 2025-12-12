package com.example.paymentApi.circle;

public record CircleWalletRequest(
        String idempotencyKey,
        String accountType,
        String[] blockchains,
        int count,
        String entitySecretCiphertext,
        String walletSetId

) {

}
