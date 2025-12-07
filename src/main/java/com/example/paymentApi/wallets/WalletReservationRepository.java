package com.example.paymentApi.wallets;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletReservationRepository extends JpaRepository<WalletReservation, String> {
}
