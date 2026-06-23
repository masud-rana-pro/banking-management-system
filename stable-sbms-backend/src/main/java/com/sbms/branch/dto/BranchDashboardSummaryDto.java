package com.sbms.branch.dto;

import java.math.BigDecimal;

public class BranchDashboardSummaryDto {

    private Long totalBranches;
    private Long activeBranches;
    private BigDecimal branchCashPosition;
    private Long pendingAssignments;
    private Long tellerLimitAlerts;
    private Long todayVaultOpened;
    private Long todayVaultClosed;
    private Long todayVaultPendingClose;

    public Long getTotalBranches() { return totalBranches; }
    public void setTotalBranches(Long totalBranches) { this.totalBranches = totalBranches; }

    public Long getActiveBranches() { return activeBranches; }
    public void setActiveBranches(Long activeBranches) { this.activeBranches = activeBranches; }

    public BigDecimal getBranchCashPosition() { return branchCashPosition; }
    public void setBranchCashPosition(BigDecimal branchCashPosition) { this.branchCashPosition = branchCashPosition; }

    public Long getPendingAssignments() { return pendingAssignments; }
    public void setPendingAssignments(Long pendingAssignments) { this.pendingAssignments = pendingAssignments; }

    public Long getTellerLimitAlerts() { return tellerLimitAlerts; }
    public void setTellerLimitAlerts(Long tellerLimitAlerts) { this.tellerLimitAlerts = tellerLimitAlerts; }

    public Long getTodayVaultOpened() { return todayVaultOpened; }
    public void setTodayVaultOpened(Long todayVaultOpened) { this.todayVaultOpened = todayVaultOpened; }

    public Long getTodayVaultClosed() { return todayVaultClosed; }
    public void setTodayVaultClosed(Long todayVaultClosed) { this.todayVaultClosed = todayVaultClosed; }

    public Long getTodayVaultPendingClose() { return todayVaultPendingClose; }
    public void setTodayVaultPendingClose(Long todayVaultPendingClose) { this.todayVaultPendingClose = todayVaultPendingClose; }
}