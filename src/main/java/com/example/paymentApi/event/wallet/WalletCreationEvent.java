package com.example.paymentApi.event.wallet;

import com.example.paymentApi.integration.circle.CircleWalletResponse;

public class WalletCreationEvent {

    private final String id;

    private final CircleWalletResponse circleWalletResponse;

    public WalletCreationEvent(CircleWalletResponse circleWalletResponse,
                               String id){
        this.circleWalletResponse = circleWalletResponse;
        this.id = id;
    }

    public CircleWalletResponse getCircleWalletResponse() {
        return circleWalletResponse;
    }

    public String getId() {
        return id;
    }
}
