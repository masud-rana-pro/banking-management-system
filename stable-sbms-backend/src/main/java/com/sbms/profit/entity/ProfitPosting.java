package com.sbms.profit.entity;

import com.sbms.account.entity.Account;
import com.sbms.profit.enums.ProfitPostingStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "profit_posting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_profit_posting_ref", columnNames = "posting_ref")
        }
)
public class ProfitPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "posting_ref", nullable = false, length = 40)
    private String postingRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private ProfitSchedule schedule;

    @Column(name = "posting_date", nullable = false)
    private LocalDate postingDate;

    @Column(name = "profit_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal profitAmount = BigDecimal.ZERO;

    @Column(name = "period_from", nullable = false)
    private LocalDate periodFrom;

    @Column(name = "period_to", nullable = false)
    private LocalDate periodTo;

    @Column(name = "posted_by", length = 120)
    private String postedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProfitPostingStatus status = ProfitPostingStatus.PENDING;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = ProfitPostingStatus.PENDING;
        }
        if (profitAmount == null) {
            profitAmount = BigDecimal.ZERO;
        }
    }

    public Long getId() {
        return id;
    }

    public String getPostingRef() {
        return postingRef;
    }

    public void setPostingRef(String postingRef) {
        this.postingRef = postingRef;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ProfitSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(ProfitSchedule schedule) {
        this.schedule = schedule;
    }

    public LocalDate getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(LocalDate postingDate) {
        this.postingDate = postingDate;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public LocalDate getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(LocalDate periodFrom) {
        this.periodFrom = periodFrom;
    }

    public LocalDate getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(LocalDate periodTo) {
        this.periodTo = periodTo;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public ProfitPostingStatus getStatus() {
        return status;
    }

    public void setStatus(ProfitPostingStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
