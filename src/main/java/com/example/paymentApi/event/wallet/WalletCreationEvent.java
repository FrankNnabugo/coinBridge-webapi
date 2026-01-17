package com.example.paymentApi.event.wallet;

import com.example.paymentApi.integration.circle.CircleWalletResponse;

public class WalletCreationEvent {

    private final String userId;

    private final String email;

    private final CircleWalletResponse circleWalletResponse;

    public WalletCreationEvent(CircleWalletResponse circleWalletResponse, String userId, String email){
        this.circleWalletResponse = circleWalletResponse;
        this.userId = userId;
        this.email = email;
    }


    public String getUserId() {
        return userId;
    }

    public CircleWalletResponse getCircleWalletResponse() {
        return circleWalletResponse;
    }

    public String getEmail() {
        return email;
    }
}
