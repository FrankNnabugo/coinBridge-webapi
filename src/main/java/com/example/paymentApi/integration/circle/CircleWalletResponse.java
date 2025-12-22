package com.example.paymentApi.integration.circle;

import java.time.LocalDateTime;

public class CircleWalletResponse {
    private String id;

    private String address;

    private String blockchain;

    private String walletSetId;

    private String accountType;

    private String referenceId;

    private String state;

    private String custodyType;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWalletSetId() {
        return walletSetId;
    }

    public void setWalletSetId(String walletSetId) {
        this.walletSetId = walletSetId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
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


    public String getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(String blockchain) {
        this.blockchain = blockchain;
    }
}
