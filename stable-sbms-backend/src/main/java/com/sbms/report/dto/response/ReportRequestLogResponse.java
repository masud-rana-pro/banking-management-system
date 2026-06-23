package com.sbms.report.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.report.enums.ReportRequestStatus;
import com.sbms.report.enums.ReportType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReportRequestLogResponse {

    private Long id;
    private Long reportId;
    private String reportCode;
    private String reportName;
    private ReportType reportType;
    private String queryKey;
    private String requestedBy;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String filterJson;
    private ReportFileResponse generatedFile;
    private ReportRequestStatus requestStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime generatedAt;
    private RecordStatus status;

    public ReportRequestLogResponse(Long id, Long reportId, String reportCode, String reportName, ReportType reportType,
                                    String queryKey, String requestedBy, LocalDate dateFrom, LocalDate dateTo, String filterJson,
                                    ReportFileResponse generatedFile, ReportRequestStatus requestStatus,
                                    LocalDateTime requestedAt, LocalDateTime generatedAt, RecordStatus status) {
        this.id = id;
        this.reportId = reportId;
        this.reportCode = reportCode;
        this.reportName = reportName;
        this.reportType = reportType;
        this.queryKey = queryKey;
        this.requestedBy = requestedBy;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.filterJson = filterJson;
        this.generatedFile = generatedFile;
        this.requestStatus = requestStatus;
        this.requestedAt = requestedAt;
        this.generatedAt = generatedAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public Long getReportId() { return reportId; }
    public String getReportCode() { return reportCode; }
    public String getReportName() { return reportName; }
    public ReportType getReportType() { return reportType; }
    public String getQueryKey() { return queryKey; }
    public String getRequestedBy() { return requestedBy; }
    public LocalDate getDateFrom() { return dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public String getFilterJson() { return filterJson; }
    public ReportFileResponse getGeneratedFile() { return generatedFile; }
    public ReportRequestStatus getRequestStatus() { return requestStatus; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public RecordStatus getStatus() { return status; }
}
