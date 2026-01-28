package com.example.paymentApi.integration.circle;

import com.example.paymentApi.shared.enums.TransferBlockchain;

public record CircleTransferRequest(
        String idempotencyKey,
        String destinationAddress,
        String[] amounts,
        String feeLevel,
        String entitySecretCiphertext,
        TransferBlockchain blockchain,
        String walletAddress,
        String tokenId

) {
}
