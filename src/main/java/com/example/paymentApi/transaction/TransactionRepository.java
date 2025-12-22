package com.example.paymentApi.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, String> {
    boolean findTransactionByTransferId(String transferId);
}
