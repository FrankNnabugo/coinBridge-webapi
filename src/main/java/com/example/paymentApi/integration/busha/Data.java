package com.example.paymentApi.integration.busha;

public class Data {

    private String id;

    private String sourceCurrency;

    private String targetCurrency;

    private String sourceAmount;

    private String targetAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public String getSourceAmount() {
        return sourceAmount;
    }

    public void setSourceAmount(String sourceAmount) {
        this.sourceAmount = sourceAmount;
    }

    public String getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(String targetAmount) {
        this.targetAmount = targetAmount;
    }
}
