package com.sbms.accounting.dto.response;

public class GlAccountSummaryResponse {

    private long totalAccounts;
    private long assetAccounts;
    private long liabilityAccounts;
    private long equityAccounts;
    private long incomeAccounts;
    private long expenseAccounts;

    public GlAccountSummaryResponse(long totalAccounts, long assetAccounts, long liabilityAccounts, long equityAccounts, long incomeAccounts, long expenseAccounts) {
        this.totalAccounts = totalAccounts;
        this.assetAccounts = assetAccounts;
        this.liabilityAccounts = liabilityAccounts;
        this.equityAccounts = equityAccounts;
        this.incomeAccounts = incomeAccounts;
        this.expenseAccounts = expenseAccounts;
    }

    public long getTotalAccounts() { return totalAccounts; }
    public long getAssetAccounts() { return assetAccounts; }
    public long getLiabilityAccounts() { return liabilityAccounts; }
    public long getEquityAccounts() { return equityAccounts; }
    public long getIncomeAccounts() { return incomeAccounts; }
    public long getExpenseAccounts() { return expenseAccounts; }
}
