package com.sbms.report.entity;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.report.enums.ReportRequestStatus;
import com.sbms.statement.entity.FileReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_request_log")
public class ReportRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private ReportDefinition report;

    @Column(name = "requested_by", nullable = false, length = 120)
    private String requestedBy;

    @Column(name = "date_from")
    private LocalDate dateFrom;

    @Column(name = "date_to")
    private LocalDate dateTo;

    @Column(name = "filter_json", columnDefinition = "TEXT")
    private String filterJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_file_id")
    private FileReference generatedFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false, length = 20)
    private ReportRequestStatus requestStatus = ReportRequestStatus.REQUESTED;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @PrePersist
    public void prePersist() {
        if (requestedAt == null) {
            requestedAt = LocalDateTime.now();
        }
        if (requestStatus == null) {
            requestStatus = ReportRequestStatus.REQUESTED;
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
    }

    public Long getId() { return id; }
    public ReportDefinition getReport() { return report; }
    public void setReport(ReportDefinition report) { this.report = report; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
    public String getFilterJson() { return filterJson; }
    public void setFilterJson(String filterJson) { this.filterJson = filterJson; }
    public FileReference getGeneratedFile() { return generatedFile; }
    public void setGeneratedFile(FileReference generatedFile) { this.generatedFile = generatedFile; }
    public ReportRequestStatus getRequestStatus() { return requestStatus; }
    public void setRequestStatus(ReportRequestStatus requestStatus) { this.requestStatus = requestStatus; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
}
