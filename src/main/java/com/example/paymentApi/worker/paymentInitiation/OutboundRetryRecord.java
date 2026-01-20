package com.example.paymentApi.worker.paymentInitiation;

import com.example.paymentApi.shared.enums.RetryStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_retry")
public class OutboundRetryRecord {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    private RetryStatus retryStatus;

    @Column(nullable = false)
    private long retryCount;

    private String reason = "reason";

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

    public long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(long retryCount) {
        this.retryCount = retryCount;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public RetryStatus getRetryStatus() {
        return retryStatus;
    }

    public void setRetryStatus(RetryStatus retryStatus) {
        this.retryStatus = retryStatus;
    }
}
