package com.example.paymentApi.ledgers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, String> {
    List<Ledger> findByAccount_idAndIdGreaterThanOrderById(
            String accountId,
            Long lastLedgerEntryId
    );
    Ledger findByTransactionId(String transactionId);

}
