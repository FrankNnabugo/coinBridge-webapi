package com.example.paymentApi.webhook.circle;

import com.example.paymentApi.shared.enums.CircleTransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookOutboundNotification {
    private String id;

    private String blockchain;

    private String walletId;

    private String tokenId;

    private String sourceAddress;

    private String destinationAddress;

    private List<BigDecimal> amounts;

    private String state;

    private CircleTransactionType transactionType;

    private String txHash;

    private String networkFee;

    public String getId() {
        return id;
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

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public List<BigDecimal> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<BigDecimal> amounts) {
        this.amounts = amounts;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public CircleTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(CircleTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(String networkFee) {
        this.networkFee = networkFee;
    }
}
