package com.example.paymentApi.transaction;

import com.example.paymentApi.shared.enums.TransactionStatus;
import com.example.paymentApi.shared.enums.TransactionType;
import com.example.paymentApi.wallets.Wallet;

public class TransactionResponse{
    private String id;

    private String blockchain;

    private Wallet circleWalletId;

    private String tokenId;

    private String destinationAddress;

    private String amounts;

    private String state;

    private String transactionType;

    public String getId() {
        return id;
    }

    public Wallet getCircleWalletId() {
        return circleWalletId;
    }

    public void setCircleWalletId(Wallet circleWalletId) {
        this.circleWalletId = circleWalletId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAmounts() {
        return amounts;
    }

    public void setAmounts(String amounts) {
        this.amounts = amounts;
    }


}
