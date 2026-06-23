package com.sbms.branch.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "vault_balance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vault_branch_date",
                        columnNames = {"branch_id", "balance_date"}
                )
        }
)
public class VaultBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "balance_date", nullable = false)
    private LocalDate balanceDate;

    @Column(name = "opening_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal openingBalance;

    @Column(name = "total_cash_in", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCashIn = BigDecimal.ZERO;

    @Column(name = "total_cash_out", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCashOut = BigDecimal.ZERO;

    @Column(name = "closing_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal closingBalance = BigDecimal.ZERO;

    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed = false;

    @Column(name = "closed_by")
    private Long closedBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (totalCashIn == null) totalCashIn = BigDecimal.ZERO;
        if (totalCashOut == null) totalCashOut = BigDecimal.ZERO;
        if (closingBalance == null) closingBalance = openingBalance == null ? BigDecimal.ZERO : openingBalance;
        if (isClosed == null) isClosed = false;
        if (status == null || status.isBlank()) status = "ACTIVE";
    }

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