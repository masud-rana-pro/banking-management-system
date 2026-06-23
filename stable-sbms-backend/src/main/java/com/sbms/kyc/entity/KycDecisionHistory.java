package com.sbms.kyc.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.kyc.enums.KycDecision;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_decision_history")
public class KycDecisionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kyc_profile_id", nullable = false)
    private KycProfile kycProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 30)
    private KycDecision decision;

    @Column(name = "decision_by", nullable = false, length = 120)
    private String decisionBy;

    @Column(name = "decision_at", nullable = false)
    private LocalDateTime decisionAt;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @PrePersist
    public void prePersist() {
        if (this.decisionAt == null) {
            this.decisionAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = RecordStatus.ACTIVE;
        }
    }

    public Long getId() {
        return id;
    }

    public KycProfile getKycProfile() {
        return kycProfile;
    }

    public void setKycProfile(KycProfile kycProfile) {
        this.kycProfile = kycProfile;
    }

    public KycDecision getDecision() {
        return decision;
    }

    public void setDecision(KycDecision decision) {
        this.decision = decision;
    }

    public String getDecisionBy() {
        return decisionBy;
    }

    public void setDecisionBy(String decisionBy) {
        this.decisionBy = decisionBy;
    }

    public LocalDateTime getDecisionAt() {
        return decisionAt;
    }

    public void setDecisionAt(LocalDateTime decisionAt) {
        this.decisionAt = decisionAt;
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
}
