package com.sbms.financing.entity;

import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "financing_disbursement",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_financing_disbursement_no", columnNames = "disbursement_no")
        }
)
public class FinancingDisbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private FinancingApplication application;

    @Column(name = "disbursement_no", nullable = false, length = 40)
    private String disbursementNo;

    @Column(name = "disbursement_date", nullable = false)
    private LocalDate disbursementDate;

    @Column(name = "disbursed_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal disbursedAmount;

    @Column(name = "credited_account_id", nullable = false)
    private Long creditedAccountId;

    @Column(name = "disbursed_by", nullable = false, length = 120)
    private String disbursedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
    }

    public Long getId() { return id; }
    public FinancingApplication getApplication() { return application; }
    public void setApplication(FinancingApplication application) { this.application = application; }
    public String getDisbursementNo() { return disbursementNo; }
    public void setDisbursementNo(String disbursementNo) { this.disbursementNo = disbursementNo; }
    public LocalDate getDisbursementDate() { return disbursementDate; }
    public void setDisbursementDate(LocalDate disbursementDate) { this.disbursementDate = disbursementDate; }
    public BigDecimal getDisbursedAmount() { return disbursedAmount; }
    public void setDisbursedAmount(BigDecimal disbursedAmount) { this.disbursedAmount = disbursedAmount; }
    public Long getCreditedAccountId() { return creditedAccountId; }
    public void setCreditedAccountId(Long creditedAccountId) { this.creditedAccountId = creditedAccountId; }
    public String getDisbursedBy() { return disbursedBy; }
    public void setDisbursedBy(String disbursedBy) { this.disbursedBy = disbursedBy; }
    public RecordStatus getStatus() { return status; }
    public void setStatus(RecordStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
