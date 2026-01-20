package com.example.paymentApi.walletToWallet.outbound;

import jakarta.validation.constraints.*;

public class OutBoundRequest {

    @NotEmpty(message = "Amounts cannot be empty")
    private String[] amounts;

    @NotNull(message = "destinationAddress cannot be null")
    @NotEmpty(message = "destinationAddress cannot be empty" )
    @NotBlank(message = "destinationAddress cannot be blank")
    private String destinationAddress;

    @NotEmpty(message = "blockchain cannot be empty" )
    @NotBlank(message = "blockchain cannot be blank")
    @NotNull(message = "blockchain is required" )
    private String blockchain;

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String[] getAmounts() {
        return amounts;
    }

    public void setAmounts(String[] amounts) {
        this.amounts = amounts;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }
}

