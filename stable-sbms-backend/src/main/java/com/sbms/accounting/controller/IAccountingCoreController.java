package com.sbms.accounting.controller;

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
import com.sbms.common.response.ApiResponse;

import java.time.LocalDate;
import java.util.List;

public interface IAccountingCoreController {

    ApiResponse<GlAccountResponse> createGlAccount(GlAccountRequest request);

    ApiResponse<List<GlAccountResponse>> getGlAccounts(String accountType, Boolean allowPosting, String status, String keyword);

    ApiResponse<GlAccountSummaryResponse> getGlAccountSummary();

    ApiResponse<GlJournalResponse> postManagementExpenseJournal(Long expenseEntryId, PostManagementExpenseJournalRequest request, String requestedBy);

    ApiResponse<GlJournalResponse> postProfitPostingJournal(Long profitPostingId, PostProfitPostingJournalRequest request, String requestedBy);

    ApiResponse<GlJournalResponse> postFinancingIncomeJournal(Long financingScheduleId, PostFinancingIncomeJournalRequest request, String requestedBy);

    ApiResponse<List<GlJournalResponse>> getJournals(String sourceType, Long sourceReferenceId, String accountCode, LocalDate dateFrom, LocalDate dateTo, Long branchId);

    ApiResponse<GlJournalSummaryResponse> getJournalSummary();

    ApiResponse<TrialBalanceResponse> getTrialBalance(LocalDate dateFrom, LocalDate dateTo, Long branchId);

    ApiResponse<ProfitLossResponse> getProfitLoss(LocalDate dateFrom, LocalDate dateTo, Long branchId);
}
