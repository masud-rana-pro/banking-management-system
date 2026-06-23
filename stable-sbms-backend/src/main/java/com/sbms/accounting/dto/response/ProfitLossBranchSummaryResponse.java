package com.sbms.accounting.dto.response;

import java.math.BigDecimal;

public class ProfitLossBranchSummaryResponse {
    private Long branchId;
    private String branchCode;
    private String branchName;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netProfit;

    public ProfitLossBranchSummaryResponse(Long branchId, String branchCode, String branchName,
                                           BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netProfit) {
        this.branchId = branchId;
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netProfit = netProfit;
    }

    public Long getBranchId() {
        return branchId;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public String getBranchName() {
        return branchName;
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
}
