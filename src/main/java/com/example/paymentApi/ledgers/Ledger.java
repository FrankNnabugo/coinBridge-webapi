package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.transaction.Transactions;
import com.example.paymentApi.wallets.Wallet;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name =  "ledger_entry")
public class Ledger{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transactions transaction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType entryType;

    @Column(nullable = false, precision = 38, scale = 8)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType asset; //may or may not be needed in production

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerStatus status; //not needed in production

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Transactions getTransaction() {
        return transaction;
    }

    public void setTransaction(Transactions transaction) {
        this.transaction = transaction;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public AssetType getAsset() {
        return asset;
    }

    public void setAsset(AssetType asset) {
        this.asset = asset;
    }

    public LedgerStatus getStatus() {
        return status;
    }

    public void setStatus(LedgerStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
