package com.sbms.kyc.entity;

import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.enums.KycReviewStatus;
import com.sbms.kyc.enums.RiskLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "kyc_profile",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_kyc_profile_customer", columnNames = "customer_id")
        }
)
public class KycProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 30)
    private RiskLevel riskLevel;

    @Column(name = "source_of_funds_note", length = 500)
    private String sourceOfFundsNote;

    @Column(name = "pep_flag", nullable = false)
    private Boolean pepFlag = false;

    @Column(name = "sanction_flag", nullable = false)
    private Boolean sanctionFlag = false;

    @Column(name = "aml_flag", nullable = false)
    private Boolean amlFlag = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", nullable = false, length = 30)
    private KycReviewStatus reviewStatus = KycReviewStatus.DRAFT;

    @Column(name = "reviewed_by", length = 120)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "remarks", length = 1000)
    private String remarks;

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
        this.createdAt = now;
        this.updatedAt = now;

        if (this.reviewStatus == null) {
            this.reviewStatus = KycReviewStatus.DRAFT;
        }

        if (this.status == null) {
            this.status = RecordStatus.ACTIVE;
        }

        if (this.pepFlag == null) {
            this.pepFlag = false;
        }

        if (this.sanctionFlag == null) {
            this.sanctionFlag = false;
        }

        if (this.amlFlag == null) {
            this.amlFlag = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getSourceOfFundsNote() {
        return sourceOfFundsNote;
    }

    public void setSourceOfFundsNote(String sourceOfFundsNote) {
        this.sourceOfFundsNote = sourceOfFundsNote;
    }

    public Boolean getPepFlag() {
        return pepFlag;
    }

    public void setPepFlag(Boolean pepFlag) {
        this.pepFlag = pepFlag;
    }

    public Boolean getSanctionFlag() {
        return sanctionFlag;
    }

    public void setSanctionFlag(Boolean sanctionFlag) {
        this.sanctionFlag = sanctionFlag;
    }

    public Boolean getAmlFlag() {
        return amlFlag;
    }

    public void setAmlFlag(Boolean amlFlag) {
        this.amlFlag = amlFlag;
    }

    public KycReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(KycReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
