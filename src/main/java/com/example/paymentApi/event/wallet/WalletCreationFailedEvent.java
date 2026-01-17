package com.example.paymentApi.event.wallet;

public class WalletCreationFailedEvent {
    private final String userId;
    private final String email;

    public WalletCreationFailedEvent(String userId, String email){
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
