package com.sbms.role.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_permission")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;

    @Column(name = "action_name", nullable = false, length = 100)
    private String actionName;

    @Column(name = "permission_code", nullable = false, length = 150)
    private String permissionCode;

    @Column(name = "display_name", nullable = false, length = 180)
    private String displayName;

    @Column(name = "allow_flag", nullable = false)
    private Boolean allowFlag = Boolean.TRUE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 120)
    private String createdBy;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (allowFlag == null) allowFlag = Boolean.TRUE;
        if (createdBy == null || createdBy.trim().isEmpty()) createdBy = "SYSTEM";
    }

    public Long getId() { return id; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }
    public String getPermissionCode() { return permissionCode; }
    public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Boolean getAllowFlag() { return allowFlag; }
    public void setAllowFlag(Boolean allowFlag) { this.allowFlag = allowFlag; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
