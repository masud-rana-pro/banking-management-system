package com.sbms.transaction.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawalRequest {

    private Long branchId;
    private Long terminalId;
    private Long debitAccountId;
    private Long tellerUserId;
    private BigDecimal amount;
    private String narration;
    private LocalDateTime transactionDate;

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Long terminalId) {
        this.terminalId = terminalId;
    }

    public Long getDebitAccountId() {
        return debitAccountId;
    }

    public void setDebitAccountId(Long debitAccountId) {
        this.debitAccountId = debitAccountId;
    }

    public Long getTellerUserId() {
        return tellerUserId;
    }

    public void setTellerUserId(Long tellerUserId) {
        this.tellerUserId = tellerUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
