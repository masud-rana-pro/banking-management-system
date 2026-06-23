package com.sbms.closing.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MonthlyClosingRunRequest {

    @NotNull
    private Long branchId;

    @NotNull
    private LocalDate closingMonth;

    private Boolean vaultClosedConfirmed;
    private Boolean profitPostedConfirmed;
    private Boolean reversalsReviewed;
    private Boolean statementsGenerated;
    private String remarks;

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }
    public LocalDate getClosingMonth() { return closingMonth; }
    public void setClosingMonth(LocalDate closingMonth) { this.closingMonth = closingMonth; }
    public Boolean getVaultClosedConfirmed() { return vaultClosedConfirmed; }
    public void setVaultClosedConfirmed(Boolean vaultClosedConfirmed) { this.vaultClosedConfirmed = vaultClosedConfirmed; }
    public Boolean getProfitPostedConfirmed() { return profitPostedConfirmed; }
    public void setProfitPostedConfirmed(Boolean profitPostedConfirmed) { this.profitPostedConfirmed = profitPostedConfirmed; }
    public Boolean getReversalsReviewed() { return reversalsReviewed; }
    public void setReversalsReviewed(Boolean reversalsReviewed) { this.reversalsReviewed = reversalsReviewed; }
    public Boolean getStatementsGenerated() { return statementsGenerated; }
    public void setStatementsGenerated(Boolean statementsGenerated) { this.statementsGenerated = statementsGenerated; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
