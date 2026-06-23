package com.sbms.atm.dto.request;

import com.sbms.atm.enums.CashBinStatus;

import java.math.BigDecimal;

public class CashBinRequest {

    private Long terminalId;
    private String binNo;
    private BigDecimal denomination;
    private Integer maxCapacity;
    private Integer currentCount;
    private CashBinStatus status;

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    public String getBinNo() {
        return binNo;
    }

    public void setBinNo(String binNo) {
        this.binNo = binNo;
    }

    public BigDecimal getDenomination() {
        return denomination;
    }

    public void setDenomination(BigDecimal denomination) {
        this.denomination = denomination;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public CashBinStatus getStatus() {
        return status;
    }

    public void setStatus(CashBinStatus status) {
        this.status = status;
    }
}