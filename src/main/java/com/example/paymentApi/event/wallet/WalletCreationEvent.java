package com.example.paymentApi.event.wallet;

import com.example.paymentApi.circle.CircleWalletResponse;

public class WalletCreationEvent {

    private final String userId;

    private final CircleWalletResponse circleWalletResponse;

    public WalletCreationEvent(CircleWalletResponse circleWalletResponse, String userId){
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
