package com.sbms.branch.dto;

import java.math.BigDecimal;

public class VaultCloseRequestDto {

    private BigDecimal totalCashIn;
    private BigDecimal totalCashOut;
    private String remarks;

    public BigDecimal getTotalCashIn() { return totalCashIn; }
    public void setTotalCashIn(BigDecimal totalCashIn) { this.totalCashIn = totalCashIn; }

    public BigDecimal getTotalCashOut() { return totalCashOut; }
    public void setTotalCashOut(BigDecimal totalCashOut) { this.totalCashOut = totalCashOut; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}