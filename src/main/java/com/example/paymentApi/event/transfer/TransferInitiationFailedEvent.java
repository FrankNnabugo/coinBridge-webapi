package com.example.paymentApi.event.transfer;

import com.example.paymentApi.walletTransaction.TransferRequest;

public class TransferInitiationFailedEvent {
    private final String userId;

    private final TransferRequest transferRequest;

    private final String reservationId;
    private final String circleWalletId;
    private final String transactionId;



    public TransferInitiationFailedEvent(String userId, TransferRequest transferRequest, String reservationId,
                                         String circleWalletId, String transactionId){
        this.userId = userId;
        this.transferRequest = transferRequest;
        this.reservationId = reservationId;
        this.circleWalletId = circleWalletId;
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public TransferRequest getOutBoundRequest() {
        return transferRequest;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getCircleWalletId() {
        return circleWalletId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
