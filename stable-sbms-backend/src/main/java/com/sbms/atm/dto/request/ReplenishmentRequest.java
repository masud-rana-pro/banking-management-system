package com.sbms.atm.dto.request;

import com.sbms.atm.enums.ReplenishmentStatus;

import java.time.LocalDate;

public class ReplenishmentRequest {

    private Long terminalId;
    private LocalDate replenishmentDate;
    private String binNo;
    private Integer quantityAdded;
    private Long performedBy;
    private String remarks;
    private ReplenishmentStatus status;

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    public LocalDate getReplenishmentDate() {
        return replenishmentDate;
    }

    public void setReplenishmentDate(LocalDate replenishmentDate) {
        this.replenishmentDate = replenishmentDate;
    }

    public String getBinNo() {
        return binNo;
    }

    public void setBinNo(String binNo) {
        this.binNo = binNo;
    }

    public Integer getQuantityAdded() {
        return quantityAdded;
    }

    public void setQuantityAdded(Integer quantityAdded) {
        this.quantityAdded = quantityAdded;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ReplenishmentStatus getStatus() {
        return status;
    }

    public void setStatus(ReplenishmentStatus status) {
        this.status = status;
    }
}