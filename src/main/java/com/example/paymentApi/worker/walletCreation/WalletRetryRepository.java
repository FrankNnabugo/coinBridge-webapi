package com.example.paymentApi.worker.walletCreation;

import com.example.paymentApi.shared.enums.RetryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRetryRepository extends JpaRepository<WalletRetryRecord, String> {
    List<WalletRetryRecord> findByStatus(RetryStatus status);
}
