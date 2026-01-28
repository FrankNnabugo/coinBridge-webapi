package com.example.paymentApi.event.transfer;


public class TransferInitiationPermanentlyFailedEvent {

    private String userId;

    private String[] amounts;

    private String reservationId;

    private String circleWalletId;

    private String transactionId;

    public TransferInitiationPermanentlyFailedEvent(String userId, String[] amounts,
                                                    String reservationId, String circleWalletId, String transactionId){
        this.userId = userId;
        this.amounts = amounts;
        this.reservationId = reservationId;
        this.circleWalletId = circleWalletId;
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public String[] getAmounts() {
        return amounts;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getCircleWalletId() {
        return circleWalletId;
    }
}
