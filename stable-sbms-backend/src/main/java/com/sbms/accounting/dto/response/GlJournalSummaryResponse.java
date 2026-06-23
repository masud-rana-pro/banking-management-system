package com.sbms.accounting.dto.response;

public class GlJournalSummaryResponse {
    private long totalJournals;
    private long managementExpenseJournals;
    private long profitPostingJournals;
    private long financingIncomeJournals;
    private long activeJournals;

    public GlJournalSummaryResponse(long totalJournals, long managementExpenseJournals, long profitPostingJournals, long financingIncomeJournals, long activeJournals) {
        this.totalJournals = totalJournals;
        this.managementExpenseJournals = managementExpenseJournals;
        this.profitPostingJournals = profitPostingJournals;
        this.financingIncomeJournals = financingIncomeJournals;
        this.activeJournals = activeJournals;
    }

    public long getTotalJournals() { return totalJournals; }
    public long getManagementExpenseJournals() { return managementExpenseJournals; }
    public long getProfitPostingJournals() { return profitPostingJournals; }
    public long getFinancingIncomeJournals() { return financingIncomeJournals; }
    public long getActiveJournals() { return activeJournals; }
}
