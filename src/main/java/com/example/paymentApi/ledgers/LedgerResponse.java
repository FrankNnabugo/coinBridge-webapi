package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.enums.*;

import java.math.BigDecimal;

public class LedgerResponse {
    private String id;

    private String walletId;

    private LedgerType entryType;

    private BigDecimal amount;

    private LedgerDirection direction;

    private ProviderType provider;

    private AssetType asset;

    private LedgerStatus status;

    private String referenceId;

    private String sourceAddress;

    private String destinationAddress;

    private CurrencyType sourceCurrency;

    private CurrencyType destinationCurrency;

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

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
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
}
