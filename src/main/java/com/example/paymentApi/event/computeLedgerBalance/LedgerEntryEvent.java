package com.example.paymentApi.event.computeLedgerBalance;

import com.example.paymentApi.ledgers.Account;

public class LedgerEntryEvent {
    private final Account account;

    public LedgerEntryEvent(Account account){
        this.account = account;
    }


    public Account getAccount() {
        return account;
    }
}
