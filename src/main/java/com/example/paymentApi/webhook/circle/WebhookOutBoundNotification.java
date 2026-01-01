package com.example.paymentApi.webhook.circle;

public class WebhookOutBoundNotification {
    private OutBoundPayload notification;

    public OutBoundPayload getNotification() {
        return notification;
    }

    public void setNotification(OutBoundPayload notification) {
        this.notification = notification;
    }
}
