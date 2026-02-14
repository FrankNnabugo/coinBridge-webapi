package com.example.paymentApi.ledgers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, String> {
    AccountBalance findByAccount_id(String accountId);
    AccountBalance findByAccount(Account account);
}
