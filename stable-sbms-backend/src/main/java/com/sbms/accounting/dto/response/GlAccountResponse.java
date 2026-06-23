package com.sbms.accounting.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public class GlAccountResponse {

    private Long id;
    private String accountCode;
    private String accountName;
    private String accountType;
    private String parentAccountCode;
    private Boolean allowPosting;
    private Boolean branchScoped;
    private RecordStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GlAccountResponse(Long id, String accountCode, String accountName, String accountType, String parentAccountCode,
                             Boolean allowPosting, Boolean branchScoped, RecordStatus status,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
        this.parentAccountCode = parentAccountCode;
        this.allowPosting = allowPosting;
        this.branchScoped = branchScoped;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getAccountCode() { return accountCode; }
    public String getAccountName() { return accountName; }
    public String getAccountType() { return accountType; }
    public String getParentAccountCode() { return parentAccountCode; }
    public Boolean getAllowPosting() { return allowPosting; }
    public Boolean getBranchScoped() { return branchScoped; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
