package com.example.paymentApi.wallets;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByCircleWalletId(String circleWalletId);
    Wallet findByUser_id(String userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.circleWalletId = :circleWalletId")
    Wallet findByCircleWalletIdForUpdate(@Param("circleWalletId") String circleWalletId);

   Optional<Wallet> findByAddress(String address);

}
