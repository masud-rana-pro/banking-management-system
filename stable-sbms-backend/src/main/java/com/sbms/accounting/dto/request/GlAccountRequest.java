package com.sbms.accounting.dto.request;

public class GlAccountRequest {

    private String accountCode;
    private String accountName;
    private String accountType;
    private String parentAccountCode;
    private Boolean allowPosting;
    private Boolean branchScoped;

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getParentAccountCode() { return parentAccountCode; }
    public void setParentAccountCode(String parentAccountCode) { this.parentAccountCode = parentAccountCode; }

    public Boolean getAllowPosting() { return allowPosting; }
    public void setAllowPosting(Boolean allowPosting) { this.allowPosting = allowPosting; }

    public Boolean getBranchScoped() { return branchScoped; }
    public void setBranchScoped(Boolean branchScoped) { this.branchScoped = branchScoped; }
}
