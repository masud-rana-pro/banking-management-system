package com.sbms.account.dto.request;

import com.sbms.account.enums.ShariahContractType;
import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;

public class AccountTypeRequest {

    private String typeCode;
    private String typeName;
    private ShariahContractType shariahContractType;
    private String currencyCode;
    private BigDecimal minimumOpeningBalance;
    private Boolean profitApplicable;
    private Boolean withdrawalAllowed;
    private RecordStatus status;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public Boolean getProfitApplicable() {
        return profitApplicable;
    }

    public void setProfitApplicable(Boolean profitApplicable) {
        this.profitApplicable = profitApplicable;
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
}
