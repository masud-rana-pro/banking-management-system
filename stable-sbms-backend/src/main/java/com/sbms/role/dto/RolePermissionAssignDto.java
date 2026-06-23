package com.sbms.role.dto;

import java.util.List;

public class RolePermissionAssignDto {

    private List<String> permissionCodes;
    private String createdBy;

    public List<String> getPermissionCodes() { return permissionCodes; }
    public void setPermissionCodes(List<String> permissionCodes) { this.permissionCodes = permissionCodes; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
