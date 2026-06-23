package com.sbms.report.dto.response;

import com.sbms.report.enums.ReportRequestStatus;
import com.sbms.report.enums.ReportType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReportResultResponse {

    private Long logId;
    private Long reportId;
    private String reportCode;
    private String reportName;
    private ReportType reportType;
    private String queryKey;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Long branchId;
    private String exportType;
    private String requestedBy;
    private String filterJson;
    private ReportRequestStatus runStatus;
    private LocalDateTime generatedAt;
    private ReportFileResponse generatedFile;
    private List<ReportMetricResponse> metrics;
    private List<ReportColumnResponse> columns;
    private List<Map<String, Object>> rows;
    private List<ReportRequestLogResponse> exportHistory;

    public ReportResultResponse(Long logId, Long reportId, String reportCode, String reportName, ReportType reportType,
                                String queryKey, LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType,
                                String requestedBy, String filterJson, ReportRequestStatus runStatus, LocalDateTime generatedAt,
                                ReportFileResponse generatedFile, List<ReportMetricResponse> metrics,
                                List<ReportColumnResponse> columns, List<Map<String, Object>> rows,
                                List<ReportRequestLogResponse> exportHistory) {
        this.logId = logId;
        this.reportId = reportId;
        this.reportCode = reportCode;
        this.reportName = reportName;
        this.reportType = reportType;
        this.queryKey = queryKey;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.branchId = branchId;
        this.exportType = exportType;
        this.requestedBy = requestedBy;
        this.filterJson = filterJson;
        this.runStatus = runStatus;
        this.generatedAt = generatedAt;
        this.generatedFile = generatedFile;
        this.metrics = metrics;
        this.columns = columns;
        this.rows = rows;
        this.exportHistory = exportHistory;
    }

    public Long getLogId() { return logId; }
    public Long getReportId() { return reportId; }
    public String getReportCode() { return reportCode; }
    public String getReportName() { return reportName; }
    public ReportType getReportType() { return reportType; }
    public String getQueryKey() { return queryKey; }
    public LocalDate getDateFrom() { return dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public Long getBranchId() { return branchId; }
    public String getExportType() { return exportType; }
    public String getRequestedBy() { return requestedBy; }
    public String getFilterJson() { return filterJson; }
    public ReportRequestStatus getRunStatus() { return runStatus; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public ReportFileResponse getGeneratedFile() { return generatedFile; }
    public List<ReportMetricResponse> getMetrics() { return metrics; }
    public List<ReportColumnResponse> getColumns() { return columns; }
    public List<Map<String, Object>> getRows() { return rows; }
    public List<ReportRequestLogResponse> getExportHistory() { return exportHistory; }
}
