package com.sbms.role.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.role.dto.RoleCreateRequestDto;
import com.sbms.role.dto.RoleDashboardSummaryDto;
import com.sbms.role.dto.RolePermissionAssignDto;
import com.sbms.role.dto.RolePermissionResponseDto;
import com.sbms.role.dto.RoleResponseDto;
import com.sbms.role.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("ROLE_MANAGEMENT_ACCESS")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @GetMapping("/dashboard-summary")
    public ApiResponse<RoleDashboardSummaryDto> getDashboardSummary() {
        return ResponseBuilder.success("Role dashboard summary fetched successfully", service.getDashboardSummary());
    }

    @RequiresPermission("ROLE_CREATE")
    @PostMapping("/create")
    public ApiResponse<RoleResponseDto> create(@RequestBody RoleCreateRequestDto dto) {
        return ResponseBuilder.success("Role created successfully", service.create(dto));
    }

    @RequiresPermission("ROLE_VIEW")
    @GetMapping("/list")
    public ApiResponse<List<RoleResponseDto>> getAll() {
        return ResponseBuilder.success("Roles fetched successfully", service.getAll());
    }

    @RequiresPermission("ROLE_VIEW")
    @GetMapping("/getall")
    public List<RoleResponseDto> getAllLegacy() {
        return service.getAll();
    }

    @RequiresPermission("ROLE_VIEW")
    @GetMapping("/dropdown")
    public ApiResponse<List<RoleResponseDto>> getDropdown() {
        return ResponseBuilder.success("Role dropdown fetched successfully", service.getDropdown());
    }

    @RequiresPermission("ROLE_VIEW")
    @GetMapping("/{id}")
    public ApiResponse<RoleResponseDto> getById(@PathVariable("id") Long id) {
        return ResponseBuilder.success("Role detail fetched successfully", service.getById(id));
    }

    @RequiresPermission("ROLE_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<RoleResponseDto> update(@PathVariable("id") Long id,
                                               @RequestBody RoleCreateRequestDto dto) {
        return ResponseBuilder.success("Role updated successfully", service.update(id, dto));
    }

    @RequiresPermission("ROLE_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<RoleResponseDto> deactivate(@PathVariable("id") Long id) {
        return ResponseBuilder.success("Role archived successfully", service.deactivate(id));
    }

    @RequiresPermission("ROLE_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<RoleResponseDto> restore(@PathVariable("id") Long id) {
        return ResponseBuilder.success("Role restored successfully", service.restore(id));
    }

    @RequiresPermission("ROLE_MAP_PERMISSIONS")
    @GetMapping("/permissions/{roleId}")
    public ApiResponse<List<RolePermissionResponseDto>> getPermissions(@PathVariable Long roleId) {
        return ResponseBuilder.success("Role permissions fetched successfully", service.getPermissions(roleId));
    }

    @RequiresPermission("ROLE_MAP_PERMISSIONS")
    @PostMapping("/{id}/permissions/map")
    public ApiResponse<List<RolePermissionResponseDto>> mapPermissions(@PathVariable Long id,
                                                                       @RequestBody RolePermissionAssignDto dto) {
        List<RolePermissionResponseDto> response = service.mapPermissions(id, dto);
        return ResponseBuilder.success("Role permissions mapped successfully", response);
    }
}
