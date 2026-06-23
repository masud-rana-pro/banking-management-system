package com.sbms.contract.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public class ContractVersionResponse {
    private final Long id;
    private final Long contractId;
    private final String contractNo;
    private final Integer versionNo;
    private final String contractText;
    private final String changeType;
    private final String changedBy;
    private final String changeNote;
    private final RecordStatus status;
    private final LocalDateTime createdAt;

    public ContractVersionResponse(Long id, Long contractId, String contractNo, Integer versionNo, String contractText,
                                   String changeType, String changedBy, String changeNote, RecordStatus status,
                                   LocalDateTime createdAt) {
        this.id = id;
        this.contractId = contractId;
        this.contractNo = contractNo;
        this.versionNo = versionNo;
        this.contractText = contractText;
        this.changeType = changeType;
        this.changedBy = changedBy;
        this.changeNote = changeNote;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getContractId() { return contractId; }
    public String getContractNo() { return contractNo; }
    public Integer getVersionNo() { return versionNo; }
    public String getContractText() { return contractText; }
    public String getChangeType() { return changeType; }
    public String getChangedBy() { return changedBy; }
    public String getChangeNote() { return changeNote; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
