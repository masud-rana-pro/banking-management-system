package com.sbms.security.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.security.enums.InvestigationCaseStatus;
import com.sbms.security.enums.InvestigationCaseType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "investigation_case", uniqueConstraints = {
        @UniqueConstraint(name = "uk_investigation_case_no", columnNames = "case_no")
})
public class InvestigationCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_no", nullable = false, length = 40)
    private String caseNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false, length = 30)
    private InvestigationCaseType caseType;

    @Column(name = "reference_module", nullable = false, length = 80)
    private String referenceModule;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(name = "opened_by", nullable = false, length = 120)
    private String openedBy;

    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_status", nullable = false, length = 30)
    private InvestigationCaseStatus caseStatus = InvestigationCaseStatus.OPEN;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "evidence_file_name", length = 255)
    private String evidenceFileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (openedAt == null) {
            openedAt = now;
        }
        if (createdAt == null) {
            createdAt = now;
        }
        if (caseStatus == null) {
            caseStatus = InvestigationCaseStatus.OPEN;
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
    }

    public Long getId() { return id; }
    public String getCaseNo() { return caseNo; }
    public void setCaseNo(String caseNo) { this.caseNo = caseNo; }
    public InvestigationCaseType getCaseType() { return caseType; }
    public void setCaseType(InvestigationCaseType caseType) { this.caseType = caseType; }
    public String getReferenceModule() { return referenceModule; }
    public void setReferenceModule(String referenceModule) { this.referenceModule = referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
    public String getOpenedBy() { return openedBy; }
    public void setOpenedBy(String openedBy) { this.openedBy = openedBy; }
    public LocalDateTime getOpenedAt() { return openedAt; }
    public void setOpenedAt(LocalDateTime openedAt) { this.openedAt = openedAt; }
    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }
    public InvestigationCaseStatus getCaseStatus() { return caseStatus; }
    public void setCaseStatus(InvestigationCaseStatus caseStatus) { this.caseStatus = caseStatus; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getEvidenceFileName() { return evidenceFileName; }
    public void setEvidenceFileName(String evidenceFileName) { this.evidenceFileName = evidenceFileName; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
