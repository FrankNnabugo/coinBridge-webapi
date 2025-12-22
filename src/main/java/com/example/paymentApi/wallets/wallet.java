package com.example.paymentApi.wallets;

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

    @Column(nullable = false, length = 36)
    private String token = "USDC";

    @Column(nullable = false, length = 36)
    private String blockchain;

    @Column(length = 36)
    private String state;

    @Column(nullable = false)
    private String custodyType;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 36)
    private String walletName; //"Polygon-Amoy"; //update migration

    @Column(nullable = false)
    private String provider="circle";

    @Column(nullable = false, length = 45)
    private String walletSetId;

    @Column(nullable = false, length = 100)
    private String circleWalletId;

    @Column(length = 100)
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

    @Column(nullable = false, length = 100)
    private String metadata;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCustodyType() {
        return custodyType;
    }

    public void setCustodyType(String custodyType) {
        this.custodyType = custodyType;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getWalletSetId() {
        return walletSetId;
    }

    public void setWalletSetId(String walletSetId) {
        this.walletSetId = walletSetId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getReservedBalance() {
        return reservedBalance;
    }

    public void setReservedBalance(BigDecimal reservedBalance) {
        this.reservedBalance = reservedBalance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public List<WalletReservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<WalletReservation> reservations) {
        this.reservations = reservations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCircleWalletId() {
        return circleWalletId;
    }

    public void setCircleWalletId(String circleWalletId) {
        this.circleWalletId = circleWalletId;
    }
}
