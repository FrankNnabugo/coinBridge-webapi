package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.enums.AssetType;
import com.example.paymentApi.wallets.Wallet;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_account")
public class Account {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(precision = 38, scale = 8)
    private BigDecimal ledgerBalance;

    @Enumerated(EnumType.STRING)
    private AssetType asset;

    @Column(precision = 38, scale = 8)
    private BigDecimal openingBalance;

    @Column(precision = 38, scale = 8)
    private BigDecimal closingBalance;

    @Column(precision = 38, scale = 8)
    private BigDecimal totalDebits;

    @Column(precision = 38, scale = 8)
    private BigDecimal totalCredits;

    private BigInteger ledgerEntryStartId; //ledger accounting startID covers from this no. of ledger to that no.

    private BigInteger ledgerEntryEndId;

    private LocalDateTime computedAt;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
