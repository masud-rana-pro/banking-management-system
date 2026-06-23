package com.sbms.depositscheme.entity;

import com.sbms.account.entity.Account;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.depositscheme.enums.DepositEnrollmentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "deposit_scheme_enrollment",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_deposit_scheme_enrollment_no", columnNames = "enrollment_no")
        }
)
public class DepositSchemeEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enrollment_no", nullable = false, length = 40)
    private String enrollmentNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheme_id", nullable = false)
    private DepositScheme scheme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_account_id", nullable = false)
    private Account linkedAccount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "installment_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_status", nullable = false, length = 40)
    private DepositEnrollmentStatus enrollmentStatus = DepositEnrollmentStatus.ACTIVE;

    @Column(name = "maturity_amount", precision = 18, scale = 2)
    private BigDecimal maturityAmount = BigDecimal.ZERO;

    @Column(name = "early_withdrawal_requested", nullable = false)
    private Boolean earlyWithdrawalRequested = false;

    @Column(name = "early_withdrawal_requested_at")
    private LocalDateTime earlyWithdrawalRequestedAt;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

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
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
        if (enrollmentStatus == null) {
            enrollmentStatus = DepositEnrollmentStatus.ACTIVE;
        }
        if (earlyWithdrawalRequested == null) {
            earlyWithdrawalRequested = false;
        }
        if (maturityAmount == null) {
            maturityAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEnrollmentNo() {
        return enrollmentNo;
    }

    public void setEnrollmentNo(String enrollmentNo) {
        this.enrollmentNo = enrollmentNo;
    }

    public DepositScheme getScheme() {
        return scheme;
    }

    public void setScheme(DepositScheme scheme) {
        this.scheme = scheme;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Account getLinkedAccount() {
        return linkedAccount;
    }

    public void setLinkedAccount(Account linkedAccount) {
        this.linkedAccount = linkedAccount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public DepositEnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(DepositEnrollmentStatus enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public BigDecimal getMaturityAmount() {
        return maturityAmount;
    }

    public void setMaturityAmount(BigDecimal maturityAmount) {
        this.maturityAmount = maturityAmount;
    }

    public Boolean getEarlyWithdrawalRequested() {
        return earlyWithdrawalRequested;
    }

    public void setEarlyWithdrawalRequested(Boolean earlyWithdrawalRequested) {
        this.earlyWithdrawalRequested = earlyWithdrawalRequested;
    }

    public LocalDateTime getEarlyWithdrawalRequestedAt() {
        return earlyWithdrawalRequestedAt;
    }

    public void setEarlyWithdrawalRequestedAt(LocalDateTime earlyWithdrawalRequestedAt) {
        this.earlyWithdrawalRequestedAt = earlyWithdrawalRequestedAt;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
