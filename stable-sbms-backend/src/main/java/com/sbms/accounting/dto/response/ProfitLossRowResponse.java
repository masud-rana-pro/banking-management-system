package com.sbms.accounting.dto.response;

import java.math.BigDecimal;

public class ProfitLossRowResponse {
    private String accountCode;
    private String accountName;
    private BigDecimal amount;

    public ProfitLossRowResponse(String accountCode, String accountName, BigDecimal amount) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.amount = amount;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
