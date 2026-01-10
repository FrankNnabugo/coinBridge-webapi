package com.example.paymentApi.integration.circle;

import com.example.paymentApi.shared.enums.BlockchainType;

import java.math.BigDecimal;
import java.util.List;

public record CircleTransferRequest(
        String idempotencyKey,
        String destinationAddress,
        BigDecimal amounts,
        String feeLevel,
        String entitySecretCiphertext,
        BlockchainType blockchain

) {
}
