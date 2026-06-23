package com.sbms.depositscheme.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.depositscheme.enums.DepositSchemeType;
import com.sbms.profit.enums.ProfitFrequency;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "deposit_scheme",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_deposit_scheme_code", columnNames = "scheme_code")
        }
)
public class DepositScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheme_code", nullable = false, length = 40)
    private String schemeCode;

    @Column(name = "scheme_name", nullable = false, length = 150)
    private String schemeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "scheme_type", nullable = false, length = 40)
    private DepositSchemeType schemeType;

    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    @Column(name = "minimum_installment", nullable = false, precision = 18, scale = 2)
    private BigDecimal minimumInstallment;

    @Column(name = "profit_ratio", nullable = false, precision = 10, scale = 4)
    private BigDecimal profitRatio;

    @Enumerated(EnumType.STRING)
    @Column(name = "profit_frequency", nullable = false, length = 30)
    private ProfitFrequency profitFrequency;

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

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public DepositSchemeType getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(DepositSchemeType schemeType) {
        this.schemeType = schemeType;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public BigDecimal getMinimumInstallment() {
        return minimumInstallment;
    }

    public void setMinimumInstallment(BigDecimal minimumInstallment) {
        this.minimumInstallment = minimumInstallment;
    }

    public BigDecimal getProfitRatio() {
        return profitRatio;
    }

    public void setProfitRatio(BigDecimal profitRatio) {
        this.profitRatio = profitRatio;
    }

    public ProfitFrequency getProfitFrequency() {
        return profitFrequency;
    }

    public void setProfitFrequency(ProfitFrequency profitFrequency) {
        this.profitFrequency = profitFrequency;
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
