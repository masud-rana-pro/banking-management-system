package com.sbms.branch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VaultOpenRequestDto {

    private Long branchId;
    private LocalDate balanceDate;
    private BigDecimal openingBalance;
    private String remarks;

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public LocalDate getBalanceDate() { return balanceDate; }
    public void setBalanceDate(LocalDate balanceDate) { this.balanceDate = balanceDate; }

    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}