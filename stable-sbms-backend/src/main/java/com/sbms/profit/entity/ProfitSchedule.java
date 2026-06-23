package com.sbms.profit.entity;

import com.sbms.account.entity.Account;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.profit.enums.ProfitFrequency;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "profit_schedule")
public class ProfitSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "profit_frequency", nullable = false, length = 30)
    private ProfitFrequency profitFrequency;

    @Column(name = "next_posting_date", nullable = false)
    private LocalDate nextPostingDate;

    @Column(name = "last_posting_date")
    private LocalDate lastPostingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ProfitFrequency getProfitFrequency() {
        return profitFrequency;
    }

    public void setProfitFrequency(ProfitFrequency profitFrequency) {
        this.profitFrequency = profitFrequency;
    }

    public LocalDate getNextPostingDate() {
        return nextPostingDate;
    }

    public void setNextPostingDate(LocalDate nextPostingDate) {
        this.nextPostingDate = nextPostingDate;
    }

    public LocalDate getLastPostingDate() {
        return lastPostingDate;
    }

    public void setLastPostingDate(LocalDate lastPostingDate) {
        this.lastPostingDate = lastPostingDate;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
