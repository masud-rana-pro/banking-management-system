package com.sbms.transaction.entity;

import com.sbms.transaction.enums.TransferMode;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fund_transfer")
public class FundTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionJournal transaction;

    @Column(name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column(name = "to_account_id", nullable = false)
    private Long toAccountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_mode", nullable = false, length = 30)
    private TransferMode transferMode = TransferMode.INTERNAL;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (transferMode == null) {
            transferMode = TransferMode.INTERNAL;
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

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public TransferMode getTransferMode() {
        return transferMode;
    }

    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
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
