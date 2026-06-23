package com.sbms.transaction.entity;

import com.sbms.transaction.enums.ChequeClearingStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cheque_clearing")
public class ChequeClearing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionJournal transaction;

    @Column(name = "credit_account_id", nullable = false)
    private Long creditAccountId;

    @Column(name = "cheque_no", nullable = false, length = 40)
    private String chequeNo;

    @Column(name = "drawee_bank", nullable = false, length = 150)
    private String draweeBank;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "cheque_status", nullable = false, length = 30)
    private ChequeClearingStatus chequeStatus = ChequeClearingStatus.CLEARED;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (chequeStatus == null) {
            chequeStatus = ChequeClearingStatus.CLEARED;
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

    public Long getCreditAccountId() {
        return creditAccountId;
    }

    public void setCreditAccountId(Long creditAccountId) {
        this.creditAccountId = creditAccountId;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public String getDraweeBank() {
        return draweeBank;
    }

    public void setDraweeBank(String draweeBank) {
        this.draweeBank = draweeBank;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ChequeClearingStatus getChequeStatus() {
        return chequeStatus;
    }

    public void setChequeStatus(ChequeClearingStatus chequeStatus) {
        this.chequeStatus = chequeStatus;
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
