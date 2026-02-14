package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.enums.CurrencyType;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    final Long LAST_PROCESSED_LEDGER_ENTRY_ID = 0L;

    @Transactional
    public void createAccountForWallet(String id){
        /**
         * Each wallet in the system gets a corresponding reconciliation account
         */
        Account account = new Account();
        account.setCurrency(CurrencyType.USDC);
        account.setAccountType(AccountType.USER_WALLET);
        accountRepository.save(account);

        /**
         * This would create a default account balance record per account.
         * used for computing ledger balance
         */
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccount(accountRepository.findById(id).orElseThrow());
        accountBalance.setLedgerBalance(BigDecimal.ZERO);
        accountBalance.setAvailableBalance(BigDecimal.ZERO);
        accountBalance.setCurrencyType(CurrencyType.USDC);
        accountBalance.setLastProcessedLedgerEntryId(LAST_PROCESSED_LEDGER_ENTRY_ID);
        accountBalanceRepository.save(accountBalance);

    }


    /**
     * This is for system reconciliation accounts
     * annotate with @PostConstruct to insert this record in the DB
     *
     */
    public void createSystemAccounts(){
        Account settlement = new Account();
        settlement.setCurrency(CurrencyType.USDC);
        settlement.setAccountType(AccountType.SETTLEMENT);
        accountRepository.save(settlement);

        Account revenue = new Account();
        revenue.setCurrency(CurrencyType.USDC);
        revenue.setAccountType(AccountType.REVENUE);
        accountRepository.save(revenue);

        Account fees = new Account();
        fees.setCurrency(CurrencyType.USDC);
        fees.setAccountType(AccountType.FEES);
        accountRepository.save(fees);

    }
}
