package com.example.paymentApi.worker.paymentInitiation;

import com.example.paymentApi.shared.enums.RetryStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OutboundRetryRepository extends JpaRepository<OutboundRetryRecord, String> {
    List<OutboundRetryRecord> findByRetryStatus(RetryStatus status);
}
