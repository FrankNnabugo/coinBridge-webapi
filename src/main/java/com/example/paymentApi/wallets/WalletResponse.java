package com.example.paymentApi.wallets;

import com.example.paymentApi.users.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class WalletResponse {
    private String id;

    private String userId;

    private User user;

    private String token;

    private String blockchain;

    private String state;

    private String custodyType;

    private String accountType;

    private String address;

    private String walletName;

    private String provider;

    private String walletSetId;

    private String circleWalletId;

    private String referenceId;

    private BigDecimal totalBalance;

    private BigDecimal reservedBalance;

    private BigDecimal availableBalance;

    private List<WalletReservation> reservations;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCircleWalletId() {
        return circleWalletId;
    }

    public void setCircleWalletId(String circleWalletId) {
        this.circleWalletId = circleWalletId;
    }
}
