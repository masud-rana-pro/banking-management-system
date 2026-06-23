package com.sbms.report.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ManagementExpenseEntryResponse {

    private Long id;
    private LocalDate expenseDate;
    private Long branchId;
    private String branchCode;
    private String branchName;
    private String expenseCategory;
    private String expenseCode;
    private BigDecimal amount;
    private String sourceType;
    private String referenceNo;
    private String remarks;
    private String createdBy;
    private LocalDateTime createdAt;

    public ManagementExpenseEntryResponse(Long id, LocalDate expenseDate, Long branchId, String branchCode, String branchName,
                                          String expenseCategory, String expenseCode, BigDecimal amount, String sourceType,
                                          String referenceNo, String remarks, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.expenseDate = expenseDate;
        this.branchId = branchId;
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.expenseCategory = expenseCategory;
        this.expenseCode = expenseCode;
        this.amount = amount;
        this.sourceType = sourceType;
        this.referenceNo = referenceNo;
        this.remarks = remarks;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public Long getBranchId() { return branchId; }
    public String getBranchCode() { return branchCode; }
    public String getBranchName() { return branchName; }
    public String getExpenseCategory() { return expenseCategory; }
    public String getExpenseCode() { return expenseCode; }
    public BigDecimal getAmount() { return amount; }
    public String getSourceType() { return sourceType; }
    public String getReferenceNo() { return referenceNo; }
    public String getRemarks() { return remarks; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
