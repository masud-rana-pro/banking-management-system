package com.sbms.report.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ReportDashboardSummaryResponse {

    private Long generatedToday;
    private Long regulatoryPending;
    private BigDecimal branchPerformanceSummary;
    private BigDecimal financingSummary;
    private BigDecimal profitSummary;
    private List<ReportUsageSummaryResponse> mostUsedReports;
    private List<ReportRequestLogResponse> recentExports;

    public ReportDashboardSummaryResponse(Long generatedToday, Long regulatoryPending, BigDecimal branchPerformanceSummary,
                                          BigDecimal financingSummary, BigDecimal profitSummary,
                                          List<ReportUsageSummaryResponse> mostUsedReports,
                                          List<ReportRequestLogResponse> recentExports) {
        this.generatedToday = generatedToday;
        this.regulatoryPending = regulatoryPending;
        this.branchPerformanceSummary = branchPerformanceSummary;
        this.financingSummary = financingSummary;
        this.profitSummary = profitSummary;
        this.mostUsedReports = mostUsedReports;
        this.recentExports = recentExports;
    }

    public Long getGeneratedToday() { return generatedToday; }
    public Long getRegulatoryPending() { return regulatoryPending; }
    public BigDecimal getBranchPerformanceSummary() { return branchPerformanceSummary; }
    public BigDecimal getFinancingSummary() { return financingSummary; }
    public BigDecimal getProfitSummary() { return profitSummary; }
    public List<ReportUsageSummaryResponse> getMostUsedReports() { return mostUsedReports; }
    public List<ReportRequestLogResponse> getRecentExports() { return recentExports; }
}
