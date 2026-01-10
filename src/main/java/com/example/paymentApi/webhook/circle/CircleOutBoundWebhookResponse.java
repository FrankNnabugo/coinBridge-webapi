package com.example.paymentApi.webhook.circle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CircleOutBoundWebhookResponse {
    private String subscriptionId;

    private String notificationId;

    private String notificationType;

    private WebhookOutBoundNotification data;

    public WebhookOutBoundNotification getData() {
        return data;
    }

    public void setData(WebhookOutBoundNotification data) {
        this.data = data;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
