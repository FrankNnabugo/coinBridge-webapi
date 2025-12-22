package com.example.paymentApi.webhook.circle;

public class CircleOutBoundWebhookResponse {
    private WebhookOutBoundNotification data;

    public WebhookOutBoundNotification getData() {
        return data;
    }

    public void setData(WebhookOutBoundNotification data) {
        this.data = data;
    }
}
