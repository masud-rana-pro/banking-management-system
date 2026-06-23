package com.sbms.financing.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancingDisbursementRequest {

    private LocalDate disbursementDate;
    private BigDecimal disbursedAmount;
    private Long creditedAccountId;
    private String disbursedBy;
    private String remarks;

    public LocalDate getDisbursementDate() { return disbursementDate; }
    public void setDisbursementDate(LocalDate disbursementDate) { this.disbursementDate = disbursementDate; }
    public BigDecimal getDisbursedAmount() { return disbursedAmount; }
    public void setDisbursedAmount(BigDecimal disbursedAmount) { this.disbursedAmount = disbursedAmount; }
    public Long getCreditedAccountId() { return creditedAccountId; }
    public void setCreditedAccountId(Long creditedAccountId) { this.creditedAccountId = creditedAccountId; }
    public String getDisbursedBy() { return disbursedBy; }
    public void setDisbursedBy(String disbursedBy) { this.disbursedBy = disbursedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
