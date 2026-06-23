package com.sbms.report.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.report.dto.request.ManagementExpenseEntryRequest;
import com.sbms.report.dto.response.ManagementExpenseEntryResponse;
import com.sbms.report.dto.response.ReportDashboardSummaryResponse;
import com.sbms.report.dto.response.ReportRequestLogResponse;
import com.sbms.report.dto.response.ReportResultResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface IReportController {

    ApiResponse<ReportDashboardSummaryResponse> dashboardSummary();

    ApiResponse<ReportResultResponse> operational(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> profitDistribution(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> managementPl(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> trialBalance(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> ledgerProfitLoss(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> financingPortfolio(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> par(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> shariahAudit(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> branch(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> kpi(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> growth(LocalDate dateFrom, LocalDate dateTo, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> loanRecovery(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ApiResponse<ReportResultResponse> monthlyClosing(LocalDate dateFrom, LocalDate dateTo, Long branchId, String exportType, String requestedBy);

    ApiResponse<ManagementExpenseEntryResponse> createManagementExpenseEntry(ManagementExpenseEntryRequest request, String requestedBy);

    ApiResponse<List<ManagementExpenseEntryResponse>> managementExpenseEntries(LocalDate dateFrom, LocalDate dateTo, Long branchId, String expenseCategory, String keyword);

    ApiResponse<List<ReportRequestLogResponse>> exportHistory(String reportType, String requestStatus, String keyword);

    ApiResponse<ReportRequestLogResponse> exportHistoryById(Long id);

    ResponseEntity<byte[]> previewExportHistoryFile(Long id);

    ResponseEntity<byte[]> downloadExportHistoryFile(Long id);
}
