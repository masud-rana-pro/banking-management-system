package com.sbms.transaction.entity;

import com.sbms.transaction.enums.CashDirection;
import com.sbms.transaction.enums.CashType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_transaction")
public class CashTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionJournal transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "cash_type", nullable = false, length = 20)
    private CashType cashType = CashType.CASH;

    @Enumerated(EnumType.STRING)
    @Column(name = "cash_direction", nullable = false, length = 20)
    private CashDirection cashDirection;

    @Column(name = "teller_user_id")
    private Long tellerUserId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (cashType == null) {
            cashType = CashType.CASH;
        }
    }

    public Long getId() {
        return id;
    }

    public TransactionJournal getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionJournal transaction) {
        this.transaction = transaction;
    }

    public CashType getCashType() {
        return cashType;
    }

    public void setCashType(CashType cashType) {
        this.cashType = cashType;
    }

    public CashDirection getCashDirection() {
        return cashDirection;
    }

    public void setCashDirection(CashDirection cashDirection) {
        this.cashDirection = cashDirection;
    }

    public Long getTellerUserId() {
        return tellerUserId;
    }

    public void setTellerUserId(Long tellerUserId) {
        this.tellerUserId = tellerUserId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
