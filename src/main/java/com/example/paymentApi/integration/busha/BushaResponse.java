package com.example.paymentApi.integration.busha;

public class BushaResponse {
    private String id;

    private String profileId;

    private String qouteId;

    private String description;

    private String sub_description;

    private String source_currency;

    private String target_currency;

    private String source_amount;

    private String target_amount;

    private String trade;

    private RateDto rate;

    private String reference;

    private String status;

    private String createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getSource_currency() {
        return source_currency;
    }

    public void setSource_currency(String source_currency) {
        this.source_currency = source_currency;
    }

    public String getTarget_currency() {
        return target_currency;
    }

    public void setTarget_currency(String target_currency) {
        this.target_currency = target_currency;
    }

    public String getSource_amount() {
        return source_amount;
    }

    public void setSource_amount(String source_amount) {
        this.source_amount = source_amount;
    }

    public String getTarget_amount() {
        return target_amount;
    }

    public void setTarget_amount(String target_amount) {
        this.target_amount = target_amount;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public RateDto getRate() {
        return rate;
    }

    public void setRate(RateDto rate) {
        this.rate = rate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getQouteId() {
        return qouteId;
    }

    public void setQouteId(String qouteId) {
        this.qouteId = qouteId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSub_description() {
        return sub_description;
    }

    public void setSub_description(String sub_description) {
        this.sub_description = sub_description;
    }
}
