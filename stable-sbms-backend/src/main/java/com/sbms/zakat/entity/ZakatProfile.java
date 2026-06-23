package com.sbms.zakat.entity;

import com.sbms.customer.entity.Customer;
import com.sbms.zakat.enums.ZakatCalculationStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "zakat_profile", uniqueConstraints = {
        @UniqueConstraint(name = "uk_zakat_profile_customer_year", columnNames = {"customer_id", "zakat_year"})
})
public class ZakatProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "zakat_year", nullable = false)
    private Integer zakatYear;

    @Column(name = "nisab_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal nisabAmount = BigDecimal.ZERO;

    @Column(name = "eligible_asset_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal eligibleAssetAmount = BigDecimal.ZERO;

    @Column(name = "zakat_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal zakatAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_status", nullable = false, length = 30)
    private ZakatCalculationStatus calculationStatus = ZakatCalculationStatus.PROFILED;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "proof_document_name", length = 255)
    private String proofDocumentName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (nisabAmount == null) {
            nisabAmount = BigDecimal.ZERO;
        }
        if (eligibleAssetAmount == null) {
            eligibleAssetAmount = BigDecimal.ZERO;
        }
        if (zakatAmount == null) {
            zakatAmount = BigDecimal.ZERO;
        }
        if (calculationStatus == null) {
            calculationStatus = ZakatCalculationStatus.PROFILED;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Integer getZakatYear() { return zakatYear; }
    public void setZakatYear(Integer zakatYear) { this.zakatYear = zakatYear; }
    public BigDecimal getNisabAmount() { return nisabAmount; }
    public void setNisabAmount(BigDecimal nisabAmount) { this.nisabAmount = nisabAmount; }
    public BigDecimal getEligibleAssetAmount() { return eligibleAssetAmount; }
    public void setEligibleAssetAmount(BigDecimal eligibleAssetAmount) { this.eligibleAssetAmount = eligibleAssetAmount; }
    public BigDecimal getZakatAmount() { return zakatAmount; }
    public void setZakatAmount(BigDecimal zakatAmount) { this.zakatAmount = zakatAmount; }
    public ZakatCalculationStatus getCalculationStatus() { return calculationStatus; }
    public void setCalculationStatus(ZakatCalculationStatus calculationStatus) { this.calculationStatus = calculationStatus; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getProofDocumentName() { return proofDocumentName; }
    public void setProofDocumentName(String proofDocumentName) { this.proofDocumentName = proofDocumentName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
