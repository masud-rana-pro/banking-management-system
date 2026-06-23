package com.sbms.user.dto;

import java.util.List;

public class UserDashboardSummaryDto {

    private Long totalUsers;
    private Long activeUsers;
    private Long lockedUsers;
    private List<UserBreakdownDto> usersByRole;
    private List<UserBreakdownDto> usersByBranch;
    private List<UserResponseDto> recentLogins;

    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }
    public Long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(Long activeUsers) { this.activeUsers = activeUsers; }
    public Long getLockedUsers() { return lockedUsers; }
    public void setLockedUsers(Long lockedUsers) { this.lockedUsers = lockedUsers; }
    public List<UserBreakdownDto> getUsersByRole() { return usersByRole; }
    public void setUsersByRole(List<UserBreakdownDto> usersByRole) { this.usersByRole = usersByRole; }
    public List<UserBreakdownDto> getUsersByBranch() { return usersByBranch; }
    public void setUsersByBranch(List<UserBreakdownDto> usersByBranch) { this.usersByBranch = usersByBranch; }
    public List<UserResponseDto> getRecentLogins() { return recentLogins; }
    public void setRecentLogins(List<UserResponseDto> recentLogins) { this.recentLogins = recentLogins; }
}
