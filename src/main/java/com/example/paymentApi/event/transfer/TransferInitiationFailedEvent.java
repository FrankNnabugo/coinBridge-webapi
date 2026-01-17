package com.example.paymentApi.event.transfer;

import com.example.paymentApi.walletToWallet.outbound.OutBoundRequest;

public class TransferInitiationFailedEvent {
    private final String userId;

    private OutBoundRequest outBoundRequest;

    public TransferInitiationFailedEvent(String userId, OutBoundRequest outBoundRequest){
        this.userId = userId;
        this.outBoundRequest = outBoundRequest;
    }

    public String getUserId() {
        return userId;
    }

    public OutBoundRequest getOutBoundRequest() {
        return outBoundRequest;
    }
}
