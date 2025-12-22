package com.example.paymentApi.webhook.circle;

import java.math.BigDecimal;

public class WebhookInboundNotification {
    private String id;

    private String blockchain;

    private String circleWalletId;

    private String tokenId;

    private String destinationAddress;

    private BigDecimal amounts;

    private String state;

    private String transactionType;

    public String getCircleWalletId() {
        return circleWalletId;
    }

    public void setCircleWalletId(String circleWalletId) {
        this.circleWalletId = circleWalletId;
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

    public BigDecimal getAmounts() {
        return amounts;
    }

    public void setAmounts(BigDecimal amounts) {
        this.amounts = amounts;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setId(String id) {
        this.id = id;
    }
}
