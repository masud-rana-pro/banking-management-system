package com.sbms.role.dto;

import java.util.List;

public class RoleDashboardSummaryDto {

    private Long totalRoles;
    private Long activeRoles;
    private Long inactiveRoles;
    private Long permissionHeavyRoles;
    private List<RoleResponseDto> recentRoles;

    public Long getTotalRoles() { return totalRoles; }
    public void setTotalRoles(Long totalRoles) { this.totalRoles = totalRoles; }
    public Long getActiveRoles() { return activeRoles; }
    public void setActiveRoles(Long activeRoles) { this.activeRoles = activeRoles; }
    public Long getInactiveRoles() { return inactiveRoles; }
    public void setInactiveRoles(Long inactiveRoles) { this.inactiveRoles = inactiveRoles; }
    public Long getPermissionHeavyRoles() { return permissionHeavyRoles; }
    public void setPermissionHeavyRoles(Long permissionHeavyRoles) { this.permissionHeavyRoles = permissionHeavyRoles; }
    public List<RoleResponseDto> getRecentRoles() { return recentRoles; }
    public void setRecentRoles(List<RoleResponseDto> recentRoles) { this.recentRoles = recentRoles; }
}
