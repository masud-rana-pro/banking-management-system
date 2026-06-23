package com.sbms.financing.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class FinancingDashboardSummaryResponse {

    private final Long pendingApplications;
    private final Long approvedApplications;
    private final BigDecimal disbursedAmount;
    private final Long overdueInstallments;
    private final List<FinancingProductMetricResponse> financingByProduct;
    private final BigDecimal charityLateFeeAmount;
    private final List<FinancingApplicationResponse> recentApplications;

    public FinancingDashboardSummaryResponse(Long pendingApplications, Long approvedApplications,
                                             BigDecimal disbursedAmount, Long overdueInstallments,
                                             List<FinancingProductMetricResponse> financingByProduct,
                                             BigDecimal charityLateFeeAmount,
                                             List<FinancingApplicationResponse> recentApplications) {
        this.pendingApplications = pendingApplications;
        this.approvedApplications = approvedApplications;
        this.disbursedAmount = disbursedAmount;
        this.overdueInstallments = overdueInstallments;
        this.financingByProduct = financingByProduct;
        this.charityLateFeeAmount = charityLateFeeAmount;
        this.recentApplications = recentApplications;
    }

    public Long getPendingApplications() { return pendingApplications; }
    public Long getApprovedApplications() { return approvedApplications; }
    public BigDecimal getDisbursedAmount() { return disbursedAmount; }
    public Long getOverdueInstallments() { return overdueInstallments; }
    public List<FinancingProductMetricResponse> getFinancingByProduct() { return financingByProduct; }
    public BigDecimal getCharityLateFeeAmount() { return charityLateFeeAmount; }
    public List<FinancingApplicationResponse> getRecentApplications() { return recentApplications; }
}
