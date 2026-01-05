package com.example.paymentApi.webhook.circle;

import com.example.paymentApi.shared.enums.CircleTransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OutBoundPayload {
    private String id;

    private String walletId;

    private String sourceAddress;

    private String destinationAddress;

    private CircleTransactionType transactionType;

    private String state;

    private List<BigDecimal> amount;

    private String txHash;

    private String networkFee;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(String networkFee) {
        this.networkFee = networkFee;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public CircleTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(CircleTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public List<BigDecimal> getAmount() {
        return amount;
    }

    public void setAmount(List<BigDecimal> amount) {
        this.amount = amount;
    }
}
