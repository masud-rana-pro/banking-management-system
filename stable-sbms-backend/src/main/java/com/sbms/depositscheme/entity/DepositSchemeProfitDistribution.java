package com.sbms.depositscheme.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.depositscheme.enums.ProfitDistributionStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposit_scheme_profit_distribution")
public class DepositSchemeProfitDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private DepositSchemeEnrollment enrollment;

    @Column(name = "distribution_no", nullable = false)
    private Integer distributionNo;

    @Column(name = "period_from", nullable = false)
    private LocalDate periodFrom;

    @Column(name = "period_to", nullable = false)
    private LocalDate periodTo;

    @Column(name = "distribution_date", nullable = false)
    private LocalDate distributionDate;

    @Column(name = "profit_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal profitAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "distribution_status", nullable = false, length = 20)
    private ProfitDistributionStatus distributionStatus = ProfitDistributionStatus.PENDING;

    @Column(name = "credited_account_id")
    private Long creditedAccountId;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
        if (distributionStatus == null) {
            distributionStatus = ProfitDistributionStatus.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public DepositSchemeEnrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(DepositSchemeEnrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Integer getDistributionNo() {
        return distributionNo;
    }

    public void setDistributionNo(Integer distributionNo) {
        this.distributionNo = distributionNo;
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

    public LocalDate getDistributionDate() {
        return distributionDate;
    }

    public void setDistributionDate(LocalDate distributionDate) {
        this.distributionDate = distributionDate;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public ProfitDistributionStatus getDistributionStatus() {
        return distributionStatus;
    }

    public void setDistributionStatus(ProfitDistributionStatus distributionStatus) {
        this.distributionStatus = distributionStatus;
    }

    public Long getCreditedAccountId() {
        return creditedAccountId;
    }

    public void setCreditedAccountId(Long creditedAccountId) {
        this.creditedAccountId = creditedAccountId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
}
