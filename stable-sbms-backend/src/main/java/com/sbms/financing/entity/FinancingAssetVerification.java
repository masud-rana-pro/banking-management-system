package com.sbms.financing.entity;

import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financing_asset_verification")
public class FinancingAssetVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private FinancingApplication application;

    @Column(name = "asset_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal assetValue;

    @Column(name = "verification_note", nullable = false, length = 1000)
    private String verificationNote;

    @Column(name = "verified_by", nullable = false, length = 120)
    private String verifiedBy;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (verifiedAt == null) {
            verifiedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
    }

    public Long getId() { return id; }
    public FinancingApplication getApplication() { return application; }
    public void setApplication(FinancingApplication application) { this.application = application; }
    public BigDecimal getAssetValue() { return assetValue; }
    public void setAssetValue(BigDecimal assetValue) { this.assetValue = assetValue; }
    public String getVerificationNote() { return verificationNote; }
    public void setVerificationNote(String verificationNote) { this.verificationNote = verificationNote; }
    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
