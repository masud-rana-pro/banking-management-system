package com.sbms.transaction.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ChequeClearingRequest {

    private Long branchId;
    private Long creditAccountId;
    private String chequeNo;
    private String draweeBank;
    private BigDecimal amount;
    private String remarks;
    private LocalDateTime transactionDate;

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getCreditAccountId() {
        return creditAccountId;
    }

    public void setCreditAccountId(Long creditAccountId) {
        this.creditAccountId = creditAccountId;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public String getDraweeBank() {
        return draweeBank;
    }

    public void setDraweeBank(String draweeBank) {
        this.draweeBank = draweeBank;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
