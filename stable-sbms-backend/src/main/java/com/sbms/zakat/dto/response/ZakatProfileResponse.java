package com.sbms.zakat.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ZakatProfileResponse {
    private final Long id;
    private final Long customerId;
    private final String customerCode;
    private final String customerName;
    private final Integer zakatYear;
    private final BigDecimal nisabAmount;
    private final BigDecimal eligibleAssetAmount;
    private final BigDecimal zakatAmount;
    private final String calculationStatus;
    private final String remarks;
    private final String proofDocumentName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ZakatProfileResponse(Long id, Long customerId, String customerCode, String customerName, Integer zakatYear,
                                BigDecimal nisabAmount, BigDecimal eligibleAssetAmount, BigDecimal zakatAmount,
                                String calculationStatus, String remarks, String proofDocumentName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.zakatYear = zakatYear;
        this.nisabAmount = nisabAmount;
        this.eligibleAssetAmount = eligibleAssetAmount;
        this.zakatAmount = zakatAmount;
        this.calculationStatus = calculationStatus;
        this.remarks = remarks;
        this.proofDocumentName = proofDocumentName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerCode() { return customerCode; }
    public String getCustomerName() { return customerName; }
    public Integer getZakatYear() { return zakatYear; }
    public BigDecimal getNisabAmount() { return nisabAmount; }
    public BigDecimal getEligibleAssetAmount() { return eligibleAssetAmount; }
    public BigDecimal getZakatAmount() { return zakatAmount; }
    public String getCalculationStatus() { return calculationStatus; }
    public String getRemarks() { return remarks; }
    public String getProofDocumentName() { return proofDocumentName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
