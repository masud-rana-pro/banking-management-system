package com.sbms.depositscheme.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DepositSchemeResponse {

    private final Long id;
    private final String schemeCode;
    private final String schemeName;
    private final String schemeType;
    private final Integer tenureMonths;
    private final BigDecimal minimumInstallment;
    private final BigDecimal profitRatio;
    private final String profitFrequency;
    private final RecordStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long activeEnrollmentCount;
    private final Long maturedEnrollmentCount;

    public DepositSchemeResponse(Long id, String schemeCode, String schemeName, String schemeType, Integer tenureMonths,
                                 BigDecimal minimumInstallment, BigDecimal profitRatio, String profitFrequency,
                                 RecordStatus status, LocalDateTime createdAt, LocalDateTime updatedAt,
                                 Long activeEnrollmentCount, Long maturedEnrollmentCount) {
        this.id = id;
        this.schemeCode = schemeCode;
        this.schemeName = schemeName;
        this.schemeType = schemeType;
        this.tenureMonths = tenureMonths;
        this.minimumInstallment = minimumInstallment;
        this.profitRatio = profitRatio;
        this.profitFrequency = profitFrequency;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.activeEnrollmentCount = activeEnrollmentCount;
        this.maturedEnrollmentCount = maturedEnrollmentCount;
    }

    public Long getId() { return id; }
    public String getSchemeCode() { return schemeCode; }
    public String getSchemeName() { return schemeName; }
    public String getSchemeType() { return schemeType; }
    public Integer getTenureMonths() { return tenureMonths; }
    public BigDecimal getMinimumInstallment() { return minimumInstallment; }
    public BigDecimal getProfitRatio() { return profitRatio; }
    public String getProfitFrequency() { return profitFrequency; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Long getActiveEnrollmentCount() { return activeEnrollmentCount; }
    public Long getMaturedEnrollmentCount() { return maturedEnrollmentCount; }
}
