package com.example.paymentApi.transaction;

import com.example.paymentApi.wallets.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, String> {
    boolean findTransactionByTransferId(String transferId);
    boolean findTransactionByReferenceId(String referencedId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transactions t WHERE t.referenceId = :transferId")
    Optional<Transactions> findByReferenceIdForLock(@Param("transferId") String transferId);
}
