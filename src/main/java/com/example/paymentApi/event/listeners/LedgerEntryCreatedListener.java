package com.example.paymentApi.event.listeners;


import com.example.paymentApi.event.computeLedgerBalance.LedgerEntryEvent;
import com.example.paymentApi.worker.ledgerBalance.LedgerBalanceWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LedgerEntryCreatedListener {
    private final LedgerBalanceWorker ledgerBalanceWorker;

    @EventListener
    public void handleLedgerCreatedEvent(LedgerEntryEvent event){
        ledgerBalanceWorker.computeBalance(event.getAccount());

    }

}
