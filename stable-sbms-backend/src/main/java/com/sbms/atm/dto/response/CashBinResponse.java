package com.sbms.atm.dto.response;

import com.sbms.atm.enums.CashBinStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CashBinResponse {

    private Long id;
    private Long terminalId;
    private String terminalCode;
    private String terminalName;
    private String binNo;
    private BigDecimal denomination;
    private Integer maxCapacity;
    private Integer currentCount;
    private BigDecimal currentAmount;
    private CashBinStatus status;
    private LocalDateTime createdAt;

    public CashBinResponse(
            Long id,
            Long terminalId,
            String terminalCode,
            String terminalName,
            String binNo,
            BigDecimal denomination,
            Integer maxCapacity,
            Integer currentCount,
            BigDecimal currentAmount,
            CashBinStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.terminalId = terminalId;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.binNo = binNo;
        this.denomination = denomination;
        this.maxCapacity = maxCapacity;
        this.currentCount = currentCount;
        this.currentAmount = currentAmount;
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

    public String getBinNo() {
        return binNo;
    }

    public BigDecimal getDenomination() {
        return denomination;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public CashBinStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}