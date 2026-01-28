package com.example.paymentApi.walletTransaction;

import com.example.paymentApi.shared.enums.TransferBlockchain;
import jakarta.validation.constraints.*;

public class TransferRequest {

    @NotEmpty(message = "Amounts cannot be empty")
    private String[] amounts;

    @NotNull(message = "destinationAddress cannot be null")
    @NotEmpty(message = "destinationAddress cannot be empty" )
    @NotBlank(message = "destinationAddress cannot be blank")
    private String destinationAddress;

    @NotNull(message = "Blockchain is required" )
    private TransferBlockchain blockchain;

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

    public TransferBlockchain getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(TransferBlockchain blockchain) {
        this.blockchain = blockchain;
    }
}

