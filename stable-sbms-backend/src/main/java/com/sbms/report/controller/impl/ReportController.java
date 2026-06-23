package com.sbms.report.controller.impl;

import com.sbms.common.aop.AopRequestContext;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.report.controller.IReportController;
import com.sbms.report.dto.request.ManagementExpenseEntryRequest;
import com.sbms.report.dto.response.ManagementExpenseEntryResponse;
import com.sbms.report.dto.response.ReportDashboardSummaryResponse;
import com.sbms.report.dto.response.ReportRequestLogResponse;
import com.sbms.report.dto.response.ReportResultResponse;
import com.sbms.report.service.IReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("REPORTING_REGULATORY_ACCESS")
public class ReportController implements IReportController {

    @Autowired
    private IReportService reportService;

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<ReportDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Report dashboard summary fetched successfully", reportService.getDashboardSummary());
    }

    @Override
    @GetMapping("/operational")
    public ApiResponse<ReportResultResponse> operational(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Operational report fetched successfully",
                reportService.getOperationalReport(dateFrom, dateTo, branchId, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/profit-distribution")
    public ApiResponse<ReportResultResponse> profitDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Profit distribution report fetched successfully",
                reportService.getProfitDistributionReport(dateFrom, dateTo, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/management-pl")
    public ApiResponse<ReportResultResponse> managementPl(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Management P&L report fetched successfully",
                reportService.getManagementPlReport(dateFrom, dateTo, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/trial-balance")
    public ApiResponse<ReportResultResponse> trialBalance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Trial balance report fetched successfully",
                reportService.getTrialBalanceReport(dateFrom, dateTo, branchId, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/ledger-profit-loss")
    public ApiResponse<ReportResultResponse> ledgerProfitLoss(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Ledger profit and loss report fetched successfully",
                reportService.getLedgerProfitLossReport(dateFrom, dateTo, branchId, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/financing-portfolio")
    public ApiResponse<ReportResultResponse> financingPortfolio(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Financing portfolio report fetched successfully",
                reportService.getFinancingPortfolioReport(dateFrom, dateTo, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/par")
    public ApiResponse<ReportResultResponse> par(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("PAR report fetched successfully",
                reportService.getParReport(dateFrom, dateTo, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/shariah-audit")
    public ApiResponse<ReportResultResponse> shariahAudit(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Shariah audit report fetched successfully",
                reportService.getShariahAuditReport(dateFrom, dateTo, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/branch")
    public ApiResponse<ReportResultResponse> branch(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Branch report fetched successfully",
                reportService.getBranchReport(dateFrom, dateTo, branchId, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/kpi")
    public ApiResponse<ReportResultResponse> kpi(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("KPI report fetched successfully",
                reportService.getKpiReport(dateFrom, dateTo, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/growth")
    public ApiResponse<ReportResultResponse> growth(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Growth report fetched successfully",
                reportService.getGrowthReport(dateFrom, dateTo, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/loan-recovery")
    public ApiResponse<ReportResultResponse> loanRecovery(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Loan recovery report fetched successfully",
                reportService.getLoanRecoveryReport(dateFrom, dateTo, branchId, exportType, requestedBy(requestedBy)));
    }

    @Override
    @GetMapping("/monthly-closing")
    public ApiResponse<ReportResultResponse> monthlyClosing(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success("Monthly closing report fetched successfully",
                reportService.getMonthlyClosingReport(dateFrom, dateTo, branchId, exportType, requestedBy(requestedBy)));
    }

    @Override
    @PostMapping("/management-expenses")
    public ApiResponse<ManagementExpenseEntryResponse> createManagementExpenseEntry(
            @RequestBody ManagementExpenseEntryRequest request,
            @RequestParam(required = false) String requestedBy
    ) {
        return ResponseBuilder.success(
                "Management expense entry created successfully",
                reportService.createManagementExpenseEntry(request, requestedBy(requestedBy))
        );
    }

    @Override
    @GetMapping("/management-expenses")
    public ApiResponse<List<ManagementExpenseEntryResponse>> managementExpenseEntries(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String expenseCategory,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success(
                "Management expense entries fetched successfully",
                reportService.getManagementExpenseEntries(dateFrom, dateTo, branchId, expenseCategory, keyword)
        );
    }

    @Override
    @GetMapping("/export-history")
    public ApiResponse<List<ReportRequestLogResponse>> exportHistory(
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String requestStatus,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Report export history fetched successfully",
                reportService.getExportHistory(reportType, requestStatus, keyword));
    }

    @Override
    @GetMapping("/export-history/{id}")
    public ApiResponse<ReportRequestLogResponse> exportHistoryById(@PathVariable Long id) {
        return ResponseBuilder.success("Report export history entry fetched successfully",
                reportService.getExportHistoryById(id));
    }

    @Override
    @GetMapping("/export-history/{id}/preview")
    public ResponseEntity<byte[]> previewExportHistoryFile(@PathVariable Long id) {
        return reportService.previewExportHistoryFile(id);
    }

    @Override
    @GetMapping("/export-history/{id}/download")
    public ResponseEntity<byte[]> downloadExportHistoryFile(@PathVariable Long id) {
        return reportService.downloadExportHistoryFile(id);
    }

    private String requestedBy(String requestedBy) {
        if (requestedBy != null && !requestedBy.trim().isEmpty()) {
            return requestedBy.trim();
        }
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? null : username.trim();
    }
}
