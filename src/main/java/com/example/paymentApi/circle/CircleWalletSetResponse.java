package com.example.paymentApi.circle;

import java.time.LocalDateTime;

public class CircleWalletSetResponse {
    private String id;

    private String custodyType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustodyType() {
        return custodyType;
    }

    public void setCustodyType(String custodyType) {
        this.custodyType = custodyType;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
