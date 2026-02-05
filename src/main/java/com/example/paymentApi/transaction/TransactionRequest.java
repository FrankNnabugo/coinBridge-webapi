package com.example.paymentApi.transaction;

import com.example.paymentApi.shared.enums.CurrencyType;
import com.example.paymentApi.shared.enums.TransactionDirection;
import com.example.paymentApi.shared.enums.TransactionStatus;
import com.example.paymentApi.shared.enums.TransactionType;
import com.example.paymentApi.users.User;
import com.example.paymentApi.wallets.Wallet;

import java.math.BigDecimal;


public class TransactionRequest {
    private String providerTransactionId;

    private Wallet wallet;

    private User user;

    private String sourceAddress;

    private String destinationAddress;

    private BigDecimal amounts;

    private TransactionStatus status;

    private TransactionType type;

    private TransactionDirection direction;

    private String referenceId;

    private CurrencyType sourceCurrency;

    private CurrencyType destinationCurrency;

    private BigDecimal balanceAfter;

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

    public BigDecimal getAmounts() {
        return amounts;
    }

    public void setAmounts(BigDecimal amounts) {
        this.amounts = amounts;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getProviderTransactionId() {
        return providerTransactionId;
    }

    public void setProviderTransactionId(String providerTransactionId) {
        this.providerTransactionId = providerTransactionId;
    }

    public TransactionDirection getDirection() {
        return direction;
    }

    public void setDirection(TransactionDirection direction) {
        this.direction = direction;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrencyType getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(CurrencyType sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public CurrencyType getDestinationCurrency() {
        return destinationCurrency;
    }

    public void setDestinationCurrency(CurrencyType destinationCurrency) {
        this.destinationCurrency = destinationCurrency;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
}
