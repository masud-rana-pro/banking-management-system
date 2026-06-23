package com.sbms.accounting.controller.impl;

import com.sbms.accounting.controller.IAccountingCoreController;
import com.sbms.accounting.dto.request.GlAccountRequest;
import com.sbms.accounting.dto.request.PostFinancingIncomeJournalRequest;
import com.sbms.accounting.dto.request.PostManagementExpenseJournalRequest;
import com.sbms.accounting.dto.request.PostProfitPostingJournalRequest;
import com.sbms.accounting.dto.response.GlAccountResponse;
import com.sbms.accounting.dto.response.GlJournalResponse;
import com.sbms.accounting.dto.response.GlJournalSummaryResponse;
import com.sbms.accounting.dto.response.GlAccountSummaryResponse;
import com.sbms.accounting.dto.response.ProfitLossResponse;
import com.sbms.accounting.dto.response.TrialBalanceResponse;
import com.sbms.accounting.service.IAccountingCoreService;
import com.sbms.common.response.ApiResponse;
import com.sbms.config.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/accounting")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("REPORTING_REGULATORY_ACCESS")
public class AccountingCoreController implements IAccountingCoreController {

    @Autowired
    private IAccountingCoreService accountingCoreService;

    @Override
    @PostMapping("/gl-accounts")
    public ApiResponse<GlAccountResponse> createGlAccount(@RequestBody GlAccountRequest request) {
        return ApiResponse.success("GL account created successfully", accountingCoreService.createGlAccount(request));
    }

    @Override
    @GetMapping("/gl-accounts")
    public ApiResponse<List<GlAccountResponse>> getGlAccounts(
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) Boolean allowPosting,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.success("GL account list loaded successfully",
                accountingCoreService.getGlAccounts(accountType, allowPosting, status, keyword));
    }

    @Override
    @GetMapping("/gl-accounts/summary")
    public ApiResponse<GlAccountSummaryResponse> getGlAccountSummary() {
        return ApiResponse.success("GL account summary loaded successfully", accountingCoreService.getGlAccountSummary());
    }

    @Override
    @PostMapping("/journals/management-expenses/{expenseEntryId}")
    public ApiResponse<GlJournalResponse> postManagementExpenseJournal(
            @PathVariable Long expenseEntryId,
            @RequestBody(required = false) PostManagementExpenseJournalRequest request,
            @RequestParam(required = false) String requestedBy
    ) {
        return ApiResponse.success(
                "Management expense posted to GL journal successfully",
                accountingCoreService.postManagementExpenseJournal(expenseEntryId, request, requestedBy)
        );
    }

    @Override
    @PostMapping("/journals/profit-postings/{profitPostingId}")
    public ApiResponse<GlJournalResponse> postProfitPostingJournal(
            @PathVariable Long profitPostingId,
            @RequestBody(required = false) PostProfitPostingJournalRequest request,
            @RequestParam(required = false) String requestedBy
    ) {
        return ApiResponse.success(
                "Profit posting posted to GL journal successfully",
                accountingCoreService.postProfitPostingJournal(profitPostingId, request, requestedBy)
        );
    }

    @Override
    @PostMapping("/journals/financing-schedules/{financingScheduleId}/income")
    public ApiResponse<GlJournalResponse> postFinancingIncomeJournal(
            @PathVariable Long financingScheduleId,
            @RequestBody(required = false) PostFinancingIncomeJournalRequest request,
            @RequestParam(required = false) String requestedBy
    ) {
        return ApiResponse.success(
                "Financing income posted to GL journal successfully",
                accountingCoreService.postFinancingIncomeJournal(financingScheduleId, request, requestedBy)
        );
    }

    @Override
    @GetMapping("/journals")
    public ApiResponse<List<GlJournalResponse>> getJournals(
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) Long sourceReferenceId,
            @RequestParam(required = false) String accountCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId
    ) {
        return ApiResponse.success("GL journal list loaded successfully", accountingCoreService.getJournals(sourceType, sourceReferenceId, accountCode, dateFrom, dateTo, branchId));
    }

    @Override
    @GetMapping("/journals/summary")
    public ApiResponse<GlJournalSummaryResponse> getJournalSummary() {
        return ApiResponse.success("GL journal summary loaded successfully", accountingCoreService.getJournalSummary());
    }

    @Override
    @GetMapping("/trial-balance")
    public ApiResponse<TrialBalanceResponse> getTrialBalance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId
    ) {
        return ApiResponse.success(
                "Trial balance loaded successfully",
                accountingCoreService.getTrialBalance(dateFrom, dateTo, branchId)
        );
    }

    @Override
    @GetMapping("/profit-loss")
    public ApiResponse<ProfitLossResponse> getProfitLoss(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long branchId
    ) {
        return ApiResponse.success(
                "Profit and loss loaded successfully",
                accountingCoreService.getProfitLoss(dateFrom, dateTo, branchId)
        );
    }
}
