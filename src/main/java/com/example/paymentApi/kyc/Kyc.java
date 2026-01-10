package com.example.paymentApi.kyc;

import com.example.paymentApi.shared.enums.KycStatus;
import com.example.paymentApi.users.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc")
public class Kyc {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, name = "id", length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus kycStatus;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String bvn;

    @Column(nullable = false)
    private String docType; //NIN, VOTER CARD, INT'L PASSPORT

    @Column(nullable = false, length = 100)
    private String docNumber;

    @Column(length = 200)
    private String docPhotoFrontUrl;

    @Column(length = 200)
    private String docPhotoBackUrl;

    @Column(nullable = false)
    private String provider;

    @Column(length = 200)
    private String referenceId; //provider reference

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime verifiedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getDocPhotoFrontUrl() {
        return docPhotoFrontUrl;
    }

    public void setDocPhotoFrontUrl(String docPhotoFrontUrl) {
        this.docPhotoFrontUrl = docPhotoFrontUrl;
    }

    public String getDocPhotoBackUrl() {
        return docPhotoBackUrl;
    }

    public void setDocPhotoBackUrl(String docPhotoBackUrl) {
        this.docPhotoBackUrl = docPhotoBackUrl;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
