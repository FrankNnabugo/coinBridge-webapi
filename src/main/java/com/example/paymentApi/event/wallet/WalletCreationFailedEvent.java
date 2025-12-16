package com.example.paymentApi.event.wallet;

public class WalletCreationFailedEvent {
    private final String userId;

    public WalletCreationFailedEvent(String userId){
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}
