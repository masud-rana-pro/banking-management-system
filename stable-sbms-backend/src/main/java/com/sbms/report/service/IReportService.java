package com.sbms.report.service;

import com.sbms.report.dto.request.ManagementExpenseEntryRequest;
import com.sbms.report.dto.response.ManagementExpenseEntryResponse;
import com.sbms.report.dto.response.ReportDashboardSummaryResponse;
import com.sbms.report.dto.response.ReportRequestLogResponse;
import com.sbms.report.dto.response.ReportResultResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface IReportService {

    ReportDashboardSummaryResponse getDashboardSummary();

    ReportResultResponse getOperationalReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ReportResultResponse getProfitDistributionReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ReportResultResponse getManagementPlReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ReportResultResponse getTrialBalanceReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ReportResultResponse getLedgerProfitLossReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ReportResultResponse getFinancingPortfolioReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ReportResultResponse getParReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ReportResultResponse getShariahAuditReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ReportResultResponse getBranchReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ReportResultResponse getKpiReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ReportResultResponse getGrowthReport(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ReportResultResponse getLoanRecoveryReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ReportResultResponse getMonthlyClosingReport(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ManagementExpenseEntryResponse createManagementExpenseEntry(ManagementExpenseEntryRequest request, String requestedBy);

    List<ManagementExpenseEntryResponse> getManagementExpenseEntries(LocalDate dateFrom, LocalDate dateTo, Long branchId, String expenseCategory, String keyword);

    List<ReportRequestLogResponse> getExportHistory(String reportType, String requestStatus, String keyword);

    ReportRequestLogResponse getExportHistoryById(Long id);

    ResponseEntity<byte[]> previewExportHistoryFile(Long id);

    ResponseEntity<byte[]> downloadExportHistoryFile(Long id);
}
