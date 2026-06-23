package com.sbms.atm.dto.response;

import com.sbms.atm.enums.ReplenishmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReplenishmentResponse {

    private Long id;
    private Long terminalId;
    private String terminalCode;
    private String terminalName;
    private LocalDate replenishmentDate;
    private String binNo;
    private BigDecimal denomination;
    private Integer quantityAdded;
    private BigDecimal amountAdded;
    private Long performedBy;
    private String remarks;
    private ReplenishmentStatus status;
    private LocalDateTime createdAt;

    public ReplenishmentResponse(
            Long id,
            Long terminalId,
            String terminalCode,
            String terminalName,
            LocalDate replenishmentDate,
            String binNo,
            BigDecimal denomination,
            Integer quantityAdded,
            BigDecimal amountAdded,
            Long performedBy,
            String remarks,
            ReplenishmentStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.terminalId = terminalId;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.replenishmentDate = replenishmentDate;
        this.binNo = binNo;
        this.denomination = denomination;
        this.quantityAdded = quantityAdded;
        this.amountAdded = amountAdded;
        this.performedBy = performedBy;
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

    public LocalDate getReplenishmentDate() {
        return replenishmentDate;
    }

    public String getBinNo() {
        return binNo;
    }

    public BigDecimal getDenomination() {
        return denomination;
    }

    public Integer getQuantityAdded() {
        return quantityAdded;
    }

    public BigDecimal getAmountAdded() {
        return amountAdded;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public ReplenishmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}