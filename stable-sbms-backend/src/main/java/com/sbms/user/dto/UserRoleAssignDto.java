package com.sbms.user.dto;

public class UserRoleAssignDto {

    private Long roleId;
    private String actionBy;

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getActionBy() { return actionBy; }
    public void setActionBy(String actionBy) { this.actionBy = actionBy; }
}
