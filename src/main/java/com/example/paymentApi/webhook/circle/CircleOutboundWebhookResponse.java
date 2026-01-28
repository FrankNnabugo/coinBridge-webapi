package com.example.paymentApi.webhook.circle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CircleOutboundWebhookResponse {

    private String subscriptionId;

    private String notificationId;

    private String notificationType; //transactions.outbound

    private WebhookInboundNotification notification;

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

    public WebhookInboundNotification getNotification() {
        return notification;
    }

    public void setNotification(WebhookInboundNotification notification) {
        this.notification = notification;
    }
}
