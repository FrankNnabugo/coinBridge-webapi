package com.example.paymentApi.ledgers;

public interface LedgerEntry {

    void postExternalInbound(LedgerRequest request);

    void postInternalInbound(LedgerRequest request);

    void postInternalReversal(LedgerRequest request);

}
