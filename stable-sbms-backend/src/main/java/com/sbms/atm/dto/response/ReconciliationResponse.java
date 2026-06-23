package com.sbms.atm.dto.response;

import com.sbms.atm.enums.ReconciliationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReconciliationResponse {

    private Long id;
    private Long terminalId;
    private String terminalCode;
    private String terminalName;
    private LocalDate reconDate;
    private BigDecimal systemAmount;
    private BigDecimal physicalAmount;
    private BigDecimal varianceAmount;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String remarks;
    private ReconciliationStatus status;
    private LocalDateTime createdAt;

    public ReconciliationResponse(
            Long id,
            Long terminalId,
            String terminalCode,
            String terminalName,
            LocalDate reconDate,
            BigDecimal systemAmount,
            BigDecimal physicalAmount,
            BigDecimal varianceAmount,
            Long approvedBy,
            LocalDateTime approvedAt,
            String remarks,
            ReconciliationStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.terminalId = terminalId;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.reconDate = reconDate;
        this.systemAmount = systemAmount;
        this.physicalAmount = physicalAmount;
        this.varianceAmount = varianceAmount;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.remarks = remarks;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public LocalDate getReconDate() {
        return reconDate;
    }

    public BigDecimal getSystemAmount() {
        return systemAmount;
    }

    public BigDecimal getPhysicalAmount() {
        return physicalAmount;
    }

    public BigDecimal getVarianceAmount() {
        return varianceAmount;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public ReconciliationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
