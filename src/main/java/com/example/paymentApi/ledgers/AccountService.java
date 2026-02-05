package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.enums.CurrencyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public void createAccount(){
        Account account = new Account();
        account.setCurrency(CurrencyType.USDC);
        account.setAccountType(AccountType.USER_WALLET);
        accountRepository.save(account);

    }

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
