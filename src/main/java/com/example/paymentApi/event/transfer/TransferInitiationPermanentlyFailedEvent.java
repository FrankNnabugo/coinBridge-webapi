package com.example.paymentApi.event.transfer;

public class TransferInitiationPermanentlyFailedEvent {

    private String userId;

    public TransferInitiationPermanentlyFailedEvent(String userId){
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
