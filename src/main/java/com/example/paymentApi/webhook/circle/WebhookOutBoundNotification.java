package com.example.paymentApi.webhook.circle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookOutBoundNotification {
    private OutBoundPayload transaction;

    public OutBoundPayload getTransaction() {
        return transaction;
    }

    public void setTransaction(OutBoundPayload transaction) {
        this.transaction = transaction;
    }
}

