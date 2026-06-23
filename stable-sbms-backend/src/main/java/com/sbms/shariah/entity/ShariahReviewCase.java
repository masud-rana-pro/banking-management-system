package com.sbms.shariah.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.shariah.enums.ShariahCaseStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shariah_review_case", uniqueConstraints = {
        @UniqueConstraint(name = "uk_shariah_review_case_no", columnNames = "case_no")
})
public class ShariahReviewCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_no", nullable = false, length = 40)
    private String caseNo;

    @Column(name = "reference_module", nullable = false, length = 80)
    private String referenceModule;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "submitted_by", nullable = false, length = 160)
    private String submittedBy;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_status", nullable = false, length = 30)
    private ShariahCaseStatus caseStatus = ShariahCaseStatus.PENDING_REVIEW;

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
        if (submittedAt == null) {
            submittedAt = now;
        }
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
        if (caseStatus == null) {
            caseStatus = ShariahCaseStatus.PENDING_REVIEW;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getCaseNo() { return caseNo; }
    public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
    public String getReferenceModule() { return referenceModule; }
    public void setReferenceModule(String referenceModule) { this.referenceModule = referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public ShariahCaseStatus getCaseStatus() { return caseStatus; }
    public void setCaseStatus(ShariahCaseStatus caseStatus) { this.caseStatus = caseStatus; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
