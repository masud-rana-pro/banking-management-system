package com.sbms.security.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.security.enums.InvestigationCaseStatus;
import com.sbms.security.enums.InvestigationCaseType;

import java.time.LocalDateTime;
import java.util.List;

public class InvestigationCaseResponse {

    private Long id;
    private String caseNo;
    private InvestigationCaseType caseType;
    private String referenceModule;
    private Long referenceId;
    private String openedBy;
    private LocalDateTime openedAt;
    private Long assignedTo;
    private String assignedUsername;
    private String assignedFullName;
    private InvestigationCaseStatus caseStatus;
    private String remarks;
    private String evidenceFileName;
    private RecordStatus status;
    private LocalDateTime createdAt;
    private List<AuditLogResponse> auditTrail;

    public InvestigationCaseResponse(Long id, String caseNo, InvestigationCaseType caseType, String referenceModule,
                                     Long referenceId, String openedBy, LocalDateTime openedAt, Long assignedTo,
                                     String assignedUsername, String assignedFullName, InvestigationCaseStatus caseStatus,
                                     String remarks, String evidenceFileName, RecordStatus status, LocalDateTime createdAt,
                                     List<AuditLogResponse> auditTrail) {
        this.id = id;
        this.caseNo = caseNo;
        this.caseType = caseType;
        this.referenceModule = referenceModule;
        this.referenceId = referenceId;
        this.openedBy = openedBy;
        this.openedAt = openedAt;
        this.assignedTo = assignedTo;
        this.assignedUsername = assignedUsername;
        this.assignedFullName = assignedFullName;
        this.caseStatus = caseStatus;
        this.remarks = remarks;
        this.evidenceFileName = evidenceFileName;
        this.status = status;
        this.createdAt = createdAt;
        this.auditTrail = auditTrail;
    }

    public Long getId() { return id; }
    public String getCaseNo() { return caseNo; }
    public InvestigationCaseType getCaseType() { return caseType; }
    public String getReferenceModule() { return referenceModule; }
    public Long getReferenceId() { return referenceId; }
    public String getOpenedBy() { return openedBy; }
    public LocalDateTime getOpenedAt() { return openedAt; }
    public Long getAssignedTo() { return assignedTo; }
    public String getAssignedUsername() { return assignedUsername; }
    public String getAssignedFullName() { return assignedFullName; }
    public InvestigationCaseStatus getCaseStatus() { return caseStatus; }
    public String getRemarks() { return remarks; }
    public String getEvidenceFileName() { return evidenceFileName; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<AuditLogResponse> getAuditTrail() { return auditTrail; }
}
