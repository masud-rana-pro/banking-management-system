package com.sbms.atm.entity;

import com.sbms.atm.enums.ReplenishmentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "terminal_replenishment")
public class TerminalReplenishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "terminal_id", nullable = false)
    private Long terminalId;

    @Column(name = "replenishment_date", nullable = false)
    private LocalDate replenishmentDate;

    @Column(name = "bin_no", nullable = false, length = 30)
    private String binNo;

    @Column(name = "denomination", nullable = false, precision = 19, scale = 2)
    private BigDecimal denomination;

    @Column(name = "quantity_added", nullable = false)
    private Integer quantityAdded;

    @Column(name = "amount_added", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountAdded;

    @Column(name = "performed_by", nullable = false)
    private Long performedBy;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ReplenishmentStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = ReplenishmentStatus.COMPLETED;
        }
    }

    public Long getId() {
        return id;
    }

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

    public BigDecimal getDenomination() {
        return denomination;
    }

    public void setDenomination(BigDecimal denomination) {
        this.denomination = denomination;
    }

    public Integer getQuantityAdded() {
        return quantityAdded;
    }

    public void setQuantityAdded(Integer quantityAdded) {
        this.quantityAdded = quantityAdded;
    }

    public BigDecimal getAmountAdded() {
        return amountAdded;
    }

    public void setAmountAdded(BigDecimal amountAdded) {
        this.amountAdded = amountAdded;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}