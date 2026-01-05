package com.example.paymentApi.webhook.circle;

import com.example.paymentApi.shared.enums.CircleTransactionType;
import com.example.paymentApi.shared.enums.TransactionType;
import com.example.paymentApi.wallets.Wallet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookInboundNotification {
    private String id;

    private String blockchain;

    private String walletId;

    private String tokenId;

    private String sourceAddress; // doesn't have

    private String destinationAddress;

    private List<BigDecimal> amounts;

    private String state;

    private CircleTransactionType transactionType;

    private String txHash;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
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

    public List<BigDecimal> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<BigDecimal> amounts) {
        this.amounts = amounts;
    }

    public CircleTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(CircleTransactionType transactionType) {
        this.transactionType = transactionType;
    }
}
