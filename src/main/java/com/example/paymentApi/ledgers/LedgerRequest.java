package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.enums.*;
import java.math.BigDecimal;

public class LedgerRequest {

    private LedgerType entryType;

    private BigDecimal amount;

    private LedgerDirection direction;

    private AssetType asset;

    private LedgerStatus status;

    private ProviderType provider;

    private String referenceId;

    private String sourceAddress;

    private String destinationAddress;

    private String sourceCurrency;

    private String destinationCurrency;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;


    public LedgerType getEntryType() {
        return entryType;
    }

    public void setEntryType(LedgerType entryType) {
        this.entryType = entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LedgerDirection getDirection() {
        return direction;
    }

    public void setDirection(LedgerDirection direction) {
        this.direction = direction;
    }

    public AssetType getAsset() {
        return asset;
    }

    public void setAsset(AssetType asset) {
        this.asset = asset;
    }

    public LedgerStatus getStatus() {
        return status;
    }

    public void setStatus(LedgerStatus status) {
        this.status = status;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public String getDestinationCurrency() {
        return destinationCurrency;
    }

    public void setDestinationCurrency(String destinationCurrency) {
        this.destinationCurrency = destinationCurrency;
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

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }
}
