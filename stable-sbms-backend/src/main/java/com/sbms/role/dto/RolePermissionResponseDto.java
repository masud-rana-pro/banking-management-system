package com.sbms.role.dto;

public class RolePermissionResponseDto {

    private String moduleName;
    private String actionName;
    private String permissionCode;
    private String displayName;
    private Boolean allowed;

    public RolePermissionResponseDto() {}

    public RolePermissionResponseDto(String moduleName, String actionName, String permissionCode, String displayName, Boolean allowed) {
        this.moduleName = moduleName;
        this.actionName = actionName;
        this.permissionCode = permissionCode;
        this.displayName = displayName;
        this.allowed = allowed;
    }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }
    public String getPermissionCode() { return permissionCode; }
    public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Boolean getAllowed() { return allowed; }
    public void setAllowed(Boolean allowed) { this.allowed = allowed; }
}
