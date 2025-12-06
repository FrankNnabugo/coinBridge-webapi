package com.example.paymentApi.wallets;

import com.example.paymentApi.shared.enums.Chain;
import com.example.paymentApi.users.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @OneToOne()
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private String token = "USDC";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Chain chain;

    @Column(nullable = false)
    private String walletAddress;

    @Column(nullable = false)
    private String walletName;

    @Column(nullable = false)
    private String provider="circle";

    @Column(nullable = false)
    private String walletId;

    @Column(nullable = false)
    private String walletSetId;

    private String accountType;

    private String referenceId;

    @Column(nullable = false, precision = 38, scale = 8)
    private BigDecimal totalBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 38, scale = 8 )
    private BigDecimal reservedBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 38, scale = 8 )
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalletReservation> reservations;

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
