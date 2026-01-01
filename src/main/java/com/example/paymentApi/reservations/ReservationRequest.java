package com.example.paymentApi.reservations;

import com.example.paymentApi.shared.enums.ReservationStatus;
import com.example.paymentApi.shared.enums.ReservationType;

import java.math.BigDecimal;

public class ReservationRequest {

    private BigDecimal amount;

    private ReservationType reservationType;

    private ReservationStatus status;

    private String transactionId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ReservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
