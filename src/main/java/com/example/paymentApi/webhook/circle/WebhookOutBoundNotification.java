package com.example.paymentApi.webhook.circle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookOutBoundNotification {
    private OutBoundPayload notification;

    public OutBoundPayload getNotification() {
        return notification;
    }

    public void setNotification(OutBoundPayload notification) {
        this.notification = notification;
    }
}
