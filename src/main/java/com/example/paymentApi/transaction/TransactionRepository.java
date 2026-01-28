package com.example.paymentApi.transaction;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionRepository extends JpaRepository<Transactions, String> {
    Transactions findByProviderTransactionId(String providerTransactionId);
    boolean existsByReferenceId(String referenceId);
    Transactions findByUser_id(String userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Transactions t WHERE t.referenceId = :referenceId")
    Transactions findByReferenceIdForLock(@Param("referenceId") String referenceId);
}
