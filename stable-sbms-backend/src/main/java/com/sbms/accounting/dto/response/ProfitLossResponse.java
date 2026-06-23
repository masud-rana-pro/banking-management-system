package com.sbms.accounting.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProfitLossResponse {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Long branchId;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netProfit;
    private List<ProfitLossRowResponse> incomeRows;
    private List<ProfitLossRowResponse> expenseRows;
    private List<ProfitLossBranchSummaryResponse> branchSummaries;

    public ProfitLossResponse(LocalDate dateFrom, LocalDate dateTo, Long branchId, BigDecimal totalIncome,
                              BigDecimal totalExpense, BigDecimal netProfit,
                              List<ProfitLossRowResponse> incomeRows, List<ProfitLossRowResponse> expenseRows,
                              List<ProfitLossBranchSummaryResponse> branchSummaries) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.branchId = branchId;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netProfit = netProfit;
        this.incomeRows = incomeRows;
        this.expenseRows = expenseRows;
        this.branchSummaries = branchSummaries;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public Long getBranchId() {
        return branchId;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public List<ProfitLossRowResponse> getIncomeRows() {
        return incomeRows;
    }

    public List<ProfitLossRowResponse> getExpenseRows() {
        return expenseRows;
    }

    public List<ProfitLossBranchSummaryResponse> getBranchSummaries() {
        return branchSummaries;
    }
}
