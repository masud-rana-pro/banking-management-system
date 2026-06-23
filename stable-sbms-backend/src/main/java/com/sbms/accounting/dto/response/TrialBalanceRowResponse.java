package com.sbms.accounting.dto.response;

import java.math.BigDecimal;

public class TrialBalanceRowResponse {
    private String accountCode;
    private String accountName;
    private String accountType;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private BigDecimal netBalance;

    public TrialBalanceRowResponse(String accountCode, String accountName, String accountType,
                                   BigDecimal totalDebit, BigDecimal totalCredit, BigDecimal netBalance) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
        this.totalDebit = totalDebit;
        this.totalCredit = totalCredit;
        this.netBalance = netBalance;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }
}
