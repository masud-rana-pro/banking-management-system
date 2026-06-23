package com.sbms.verification.entity;

import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_verification_status")
public class ContactVerificationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference_module", nullable = false, length = 60)
    private String referenceModule;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "contact_type", nullable = false, length = 20)
    private String contactType;

    @Column(name = "contact_value", nullable = false, length = 160)
    private String contactValue;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = true;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by", length = 120)
    private String verifiedBy;

    @Column(name = "verification_method", length = 50)
    private String verificationMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_verification_request_id")
    private OtpVerificationRequest lastVerificationRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (isPrimary == null) isPrimary = true;
        if (isVerified == null) isVerified = false;
        if (status == null) status = RecordStatus.ACTIVE;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getReferenceModule() { return referenceModule; }
    public void setReferenceModule(String referenceModule) { this.referenceModule = referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }
    public String getContactValue() { return contactValue; }
    public void setContactValue(String contactValue) { this.contactValue = contactValue; }
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean primary) { isPrimary = primary; }
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean verified) { isVerified = verified; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
    public String getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; }
    public OtpVerificationRequest getLastVerificationRequest() { return lastVerificationRequest; }
    public void setLastVerificationRequest(OtpVerificationRequest lastVerificationRequest) { this.lastVerificationRequest = lastVerificationRequest; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
