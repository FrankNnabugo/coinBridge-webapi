package com.example.paymentApi.event.wallet;

import com.example.paymentApi.integration.CircleWalletResponse;

public class WalletCreatedEvent {

    private final String userId;

    private final CircleWalletResponse circleWalletResponse;

    public WalletCreatedEvent(CircleWalletResponse circleWalletResponse, String userId){
        this.circleWalletResponse = circleWalletResponse;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public CircleWalletResponse getCircleWalletResponse() {
        return circleWalletResponse;
    }
}
