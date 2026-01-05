package com.example.paymentApi.webhook.circle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CircleOutBoundWebhookResponse {
    private WebhookOutBoundNotification data;

    public WebhookOutBoundNotification getData() {
        return data;
    }

    public void setData(WebhookOutBoundNotification data) {
        this.data = data;
    }
}
