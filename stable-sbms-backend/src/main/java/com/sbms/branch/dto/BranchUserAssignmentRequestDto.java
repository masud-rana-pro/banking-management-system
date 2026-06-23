package com.sbms.branch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BranchUserAssignmentRequestDto {

    @NotNull(message = "Branch is required")
    private Long branchId;

    @NotNull(message = "User is required")
    private Long userId;

    @NotBlank(message = "Assignment role is required")
    private String assignmentRole;

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    private LocalDate toDate;
    private Boolean isPrimary;
    private String status;

    public Long getBranchId() { return branchId; }
    public void setBranchId(Long branchId) { this.branchId = branchId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAssignmentRole() { return assignmentRole; }
    public void setAssignmentRole(String assignmentRole) { this.assignmentRole = assignmentRole; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean primary) { isPrimary = primary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}