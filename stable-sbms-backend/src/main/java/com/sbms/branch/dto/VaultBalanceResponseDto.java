package com.sbms.branch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class VaultBalanceResponseDto {

    private Long id;
    private Long branchId;
    private LocalDate balanceDate;
    private BigDecimal openingBalance;
    private BigDecimal totalCashIn;
    private BigDecimal totalCashOut;
    private BigDecimal closingBalance;
    private Boolean isClosed;
    private Long closedBy;
    private LocalDateTime closedAt;
    private String remarks;
    private String status;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public LocalDate getBalanceDate() { return balanceDate; }
    public void setBalanceDate(LocalDate balanceDate) { this.balanceDate = balanceDate; }

    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }

    public BigDecimal getTotalCashIn() { return totalCashIn; }
    public void setTotalCashIn(BigDecimal totalCashIn) { this.totalCashIn = totalCashIn; }

    public BigDecimal getTotalCashOut() { return totalCashOut; }
    public void setTotalCashOut(BigDecimal totalCashOut) { this.totalCashOut = totalCashOut; }

    public BigDecimal getClosingBalance() { return closingBalance; }
    public void setClosingBalance(BigDecimal closingBalance) { this.closingBalance = closingBalance; }

    public Boolean getIsClosed() { return isClosed; }
    public void setIsClosed(Boolean closed) { isClosed = closed; }

    public Long getClosedBy() { return closedBy; }
    public void setClosedBy(Long closedBy) { this.closedBy = closedBy; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}