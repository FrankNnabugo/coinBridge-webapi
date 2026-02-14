package com.example.paymentApi.worker.ledgerBalance;

import com.example.paymentApi.ledgers.*;
import com.example.paymentApi.shared.enums.CurrencyType;
import com.example.paymentApi.shared.enums.EntryType;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerBalanceWorker {

    private final LedgerRepository ledgerRepository;
    private final AccountBalanceRepository accountBalanceRepository;

    public void computeBalance(Account account){

        AccountBalance accountBalance = accountBalanceRepository.findByAccount(account);
        List<Ledger> newEntries = ledgerRepository.findByAccount_idAndIdGreaterThanOrderById(accountBalance.getId(),
                accountBalance.getLastProcessedLedgerEntryId());

        if(newEntries.isEmpty()) return;
        for (Ledger entry: newEntries){
            BigDecimal amount = entry.getAmount();
            if(entry.getEntryType()== EntryType.CREDIT){
                accountBalance.setLedgerBalance(accountBalance.getLedgerBalance().add(amount));
                accountBalance.setAvailableBalance(accountBalance.getAvailableBalance().add(amount));
            }

            if(entry.getEntryType()==EntryType.DEBIT){
                accountBalance.setLedgerBalance(accountBalance.getLedgerBalance().subtract(amount));
                accountBalance.setAvailableBalance(accountBalance.getAvailableBalance().subtract(amount));
            }
            accountBalance.setLastProcessedLedgerEntryId(entry.getId());
            accountBalance.setUpdatedAt(LocalDateTime.now());
            accountBalanceRepository.save(accountBalance);
        }
    }

    /**
     *
     * To run aggregation of ledger balanceTotal
     * total credit
     * total debit
     * from lastEntry
     * update ledger balance
     * update available balance
     * update ledger account table with these for internal team query and reconciliation
     */
}
