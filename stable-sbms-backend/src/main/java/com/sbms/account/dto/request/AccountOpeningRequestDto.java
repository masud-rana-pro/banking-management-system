package com.sbms.account.dto.request;

import com.sbms.account.enums.AccountOpeningRequestStatus;
import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountOpeningRequestDto {

    private Long customerId;
    private Long accountTypeId;
    private Long branchId;
    private LocalDate requestedDate;
    private BigDecimal initialDepositAmount;
    private AccountOpeningRequestStatus requestStatus;
    private String remarks;
    private String applicantImageName;
    private RecordStatus status;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public BigDecimal getInitialDepositAmount() {
        return initialDepositAmount;
    }

    public void setInitialDepositAmount(BigDecimal initialDepositAmount) {
        this.initialDepositAmount = initialDepositAmount;
    }

    public AccountOpeningRequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(AccountOpeningRequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getApplicantImageName() {
        return applicantImageName;
    }

    public void setApplicantImageName(String applicantImageName) {
        this.applicantImageName = applicantImageName;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(RecordStatus status) {
        this.status = status;
    }
}
