package com.sbms.financing.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancingRepaymentCollectionRequest {

    private BigDecimal paymentAmount;
    private LocalDate paymentDate;
    private String remarks;
    private String collectedBy;

    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getCollectedBy() { return collectedBy; }
    public void setCollectedBy(String collectedBy) { this.collectedBy = collectedBy; }
}
