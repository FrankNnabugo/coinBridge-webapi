package com.example.paymentApi.kyc;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KycRepository extends JpaRepository<Kyc, String> {
}
