package com.sbms.report.dto.response;

public class ReportUsageSummaryResponse {

    private String reportCode;
    private String reportName;
    private String queryKey;
    private Long usageCount;

    public ReportUsageSummaryResponse(String reportCode, String reportName, String queryKey, Long usageCount) {
        this.reportCode = reportCode;
        this.reportName = reportName;
        this.queryKey = queryKey;
        this.usageCount = usageCount;
    }

    public String getReportCode() { return reportCode; }
    public String getReportName() { return reportName; }
    public String getQueryKey() { return queryKey; }
    public Long getUsageCount() { return usageCount; }
}
