package com.example.paymentApi.transaction;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, String> {
    Transactions findByTransferId(String referenceId);
    boolean existsByReferenceId(String referenceId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transactions t WHERE t.referenceId = :referenceId")
    Transactions findByReferenceIdForLock(@Param("referenceId") String referenceId);

}
