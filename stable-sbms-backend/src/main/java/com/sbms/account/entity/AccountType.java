package com.sbms.account.entity;

import com.sbms.account.enums.ShariahContractType;
import com.sbms.customer.enums.RecordStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "account_type",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_account_type_code", columnNames = "type_code")
        }
)
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_code", nullable = false, length = 30)
    private String typeCode;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "type_name", nullable = false, length = 100)
    private String typeName;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "account_category", nullable = false, length = 100)
    private String accountCategory;

    @Column(name = "account_subcategory", length = 100)
    private String accountSubcategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "shariah_contract_type", nullable = false, length = 40)
    private ShariahContractType shariahContractType;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    @Column(name = "minimum_opening_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal minimumOpeningBalance = BigDecimal.ZERO;

    @Column(name = "minimum_balance", precision = 18, scale = 2)
    private BigDecimal minimumBalance = BigDecimal.ZERO;

    @Column(name = "profit_applicable", nullable = false)
    private Boolean profitApplicable = Boolean.FALSE;

    @Column(name = "psr_required", nullable = false)
    private Boolean psrRequired = Boolean.FALSE;

    @Column(name = "withdrawal_allowed", nullable = false)
    private Boolean withdrawalAllowed = Boolean.TRUE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status = RecordStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
        if (minimumOpeningBalance == null) {
            minimumOpeningBalance = BigDecimal.ZERO;
        }
        if (minimumBalance == null) {
            minimumBalance = minimumOpeningBalance;
        }
        if (profitApplicable == null) {
            profitApplicable = Boolean.FALSE;
        }
        if (psrRequired == null) {
            psrRequired = profitApplicable;
        }
        if (withdrawalAllowed == null) {
            withdrawalAllowed = Boolean.TRUE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountCategory() {
        return accountCategory;
    }

    public void setAccountCategory(String accountCategory) {
        this.accountCategory = accountCategory;
    }

    public String getAccountSubcategory() {
        return accountSubcategory;
    }

    public void setAccountSubcategory(String accountSubcategory) {
        this.accountSubcategory = accountSubcategory;
    }

    public ShariahContractType getShariahContractType() {
        return shariahContractType;
    }

    public void setShariahContractType(ShariahContractType shariahContractType) {
        this.shariahContractType = shariahContractType;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getMinimumOpeningBalance() {
        return minimumOpeningBalance;
    }

    public void setMinimumOpeningBalance(BigDecimal minimumOpeningBalance) {
        this.minimumOpeningBalance = minimumOpeningBalance;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public Boolean getProfitApplicable() {
        return profitApplicable;
    }

    public void setProfitApplicable(Boolean profitApplicable) {
        this.profitApplicable = profitApplicable;
    }

    public Boolean getPsrRequired() {
        return psrRequired;
    }

    public void setPsrRequired(Boolean psrRequired) {
        this.psrRequired = psrRequired;
    }

    public Boolean getWithdrawalAllowed() {
        return withdrawalAllowed;
    }

    public void setWithdrawalAllowed(Boolean withdrawalAllowed) {
        this.withdrawalAllowed = withdrawalAllowed;
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
