package com.sbms.accounting.service;

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

import java.time.LocalDate;
import java.util.List;

public interface IAccountingCoreService {

    GlAccountResponse createGlAccount(GlAccountRequest request);

    List<GlAccountResponse> getGlAccounts(String accountType, Boolean allowPosting, String status, String keyword);

    GlAccountSummaryResponse getGlAccountSummary();

    GlJournalResponse postManagementExpenseJournal(Long expenseEntryId, PostManagementExpenseJournalRequest request, String requestedBy);

    GlJournalResponse postProfitPostingJournal(Long profitPostingId, PostProfitPostingJournalRequest request, String requestedBy);

    GlJournalResponse postFinancingIncomeJournal(Long financingScheduleId, PostFinancingIncomeJournalRequest request, String requestedBy);

    List<GlJournalResponse> getJournals(String sourceType, Long sourceReferenceId, String accountCode, LocalDate dateFrom, LocalDate dateTo, Long branchId);

    GlJournalSummaryResponse getJournalSummary();

    TrialBalanceResponse getTrialBalance(LocalDate dateFrom, LocalDate dateTo, Long branchId);

    ProfitLossResponse getProfitLoss(LocalDate dateFrom, LocalDate dateTo, Long branchId);
}
