package com.example.paymentApi.event.wallet;

public class WalletCreationPermanentlyFailedEvent {
    private final String userId;

    public WalletCreationPermanentlyFailedEvent(String userId){
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
