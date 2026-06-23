package com.sbms.role.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RoleResponseDto {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long assignedUserCount;
    private Long permissionCount;
    private List<RolePermissionResponseDto> permissions;

    public RoleResponseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public Long getAssignedUserCount() { return assignedUserCount; }
    public void setAssignedUserCount(Long assignedUserCount) { this.assignedUserCount = assignedUserCount; }

    public Long getPermissionCount() { return permissionCount; }
    public void setPermissionCount(Long permissionCount) { this.permissionCount = permissionCount; }

    public List<RolePermissionResponseDto> getPermissions() { return permissions; }
    public void setPermissions(List<RolePermissionResponseDto> permissions) { this.permissions = permissions; }
}
