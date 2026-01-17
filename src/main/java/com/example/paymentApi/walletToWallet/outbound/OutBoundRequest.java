package com.example.paymentApi.walletToWallet.outbound;

import com.example.paymentApi.shared.enums.BlockchainType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class OutBoundRequest {

    @NotEmpty(message = "Amount cannot be empty")
    @NotNull(message = "Amount cannot be null")
    @NotBlank(message = "Amount cannot be blank")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
    private BigDecimal amounts;

    @NotNull(message = "destinationAddress cannot be null")
    @NotEmpty(message = "destinationAddress cannot be empty" )
    @NotBlank(message = "destinationAddress cannot be blank")
    private String destinationAddress;

    @NotNull(message = "blockchain cannot be null")
    @NotEmpty(message = "blockchain cannot be empty" )
    @NotBlank(message = "blockchain cannot be blank")
    private BlockchainType blockchain;

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public BlockchainType getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(BlockchainType blockchain) {
        this.blockchain = blockchain;
    }

    public BigDecimal getAmounts() {
        return amounts;
    }

    public void setAmounts(BigDecimal amounts) {
        this.amounts = amounts;
    }
}

