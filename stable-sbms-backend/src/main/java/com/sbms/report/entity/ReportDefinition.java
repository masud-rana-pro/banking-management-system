package com.sbms.report.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.report.enums.ReportType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_definition", uniqueConstraints = {
        @UniqueConstraint(name = "uk_report_definition_code", columnNames = "report_code"),
        @UniqueConstraint(name = "uk_report_definition_query_key", columnNames = "query_key")
})
public class ReportDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_code", nullable = false, length = 40)
    private String reportCode;

    @Column(name = "report_name", nullable = false, length = 180)
    private String reportName;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 30)
    private ReportType reportType;

    @Column(name = "query_key", nullable = false, length = 80)
    private String queryKey;

    @Column(name = "export_types", nullable = false, length = 80)
    private String exportTypes;

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
    }

    public Long getId() { return id; }
    public String getReportCode() { return reportCode; }
    public void setReportCode(String reportCode) { this.reportCode = reportCode; }
    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    public String getQueryKey() { return queryKey; }
    public void setQueryKey(String queryKey) { this.queryKey = queryKey; }
    public String getExportTypes() { return exportTypes; }
    public void setExportTypes(String exportTypes) { this.exportTypes = exportTypes; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
