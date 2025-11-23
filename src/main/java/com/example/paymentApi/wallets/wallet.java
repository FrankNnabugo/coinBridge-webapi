package com.example.paymentApi.wallets;

import com.example.paymentApi.shared.enums.Chain;
import com.example.paymentApi.shared.enums.Token;
import com.example.paymentApi.users.User;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformers;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallets")
public class wallet{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @OneToOne()
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Token token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Chain chain;

    @Column(nullable = false)
    private String walletAddress;

    @Column(nullable = false)
    private String walletProvider="circle";

    @Column(nullable = false)
    private String providerWalletId;

    @Column(nullable = false, length = 36)
    private BigDecimal totalBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String status = "active";

    @Column(columnDefinition = "jsonb")
    private String metadata;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;










}
