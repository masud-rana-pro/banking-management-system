package com.sbms.atm.dto.request;

import com.sbms.atm.enums.ReconciliationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReconciliationRequest {

    private Long terminalId;
    private LocalDate reconDate;
    private BigDecimal systemAmount;
    private BigDecimal physicalAmount;
    private Long approvedBy;
    private String remarks;
    private ReconciliationStatus status;

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    public LocalDate getReconDate() {
        return reconDate;
    }

    public void setReconDate(LocalDate reconDate) {
        this.reconDate = reconDate;
    }

    public BigDecimal getSystemAmount() {
        return systemAmount;
    }

    public void setSystemAmount(BigDecimal systemAmount) {
        this.systemAmount = systemAmount;
    }

    public BigDecimal getPhysicalAmount() {
        return physicalAmount;
    }

    public void setPhysicalAmount(BigDecimal physicalAmount) {
        this.physicalAmount = physicalAmount;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ReconciliationStatus getStatus() {
        return status;
    }

    public void setStatus(ReconciliationStatus status) {
        this.status = status;
    }
}
