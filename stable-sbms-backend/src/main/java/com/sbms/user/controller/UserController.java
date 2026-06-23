package com.sbms.user.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.user.dto.*;
import com.sbms.user.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(originPatterns = { "http://localhost:*", "http://127.0.0.1:*" })
@RequiresPermission("USER_MANAGEMENT_ACCESS")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard-summary")
    public ApiResponse<UserDashboardSummaryDto> dashboardSummary() {
        return ResponseBuilder.success("User dashboard summary loaded successfully", userService.getDashboardSummary());
    }

    @RequiresPermission("USER_CREATE")
    @PostMapping("/create")
    public ApiResponse<UserResponseDto> create(@RequestBody UserCreateRequestDto dto) {
        return ResponseBuilder.success("User created successfully", userService.create(dto));
    }

    @RequiresPermission("USER_VIEW")
    @GetMapping("/list")
    public ApiResponse<List<UserResponseDto>> list(@RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long roleId,
            @RequestParam(required = false) Long branchId) {
        return ResponseBuilder.success("User list loaded successfully",
                userService.getAll(search, status, roleId, branchId));
    }

    @RequiresPermission("USER_VIEW")
    @GetMapping("/getall")
    public List<UserResponseDto> legacyList() {
        return userService.getAll(null, null, null, null);
    }

    @RequiresPermission("USER_VIEW")
    @GetMapping("/getall1")
    public ResponseEntity<List<UserResponseDto>> legacyList1() {
        List<UserResponseDto> users = userService.getAll(null, null, null, null);
        return ResponseEntity.ok(users);
    }

    @RequiresPermission("USER_VIEW")
    @GetMapping("/dropdown")
    public ApiResponse<List<UserResponseDto>> dropdown() {
        return ResponseBuilder.success("User dropdown loaded successfully", userService.getDropdown());
    }

    @RequiresPermission("USER_VIEW")
    @GetMapping("/search")
    public ApiResponse<List<UserResponseDto>> search(@RequestParam String keyword) {
        return ResponseBuilder.success("User search loaded successfully", userService.search(keyword));
    }

    @RequiresPermission("USER_VIEW")
    @GetMapping("/{id}")
    public ApiResponse<UserResponseDto> getById(@PathVariable Long id) {
        return ResponseBuilder.success("User detail loaded successfully", userService.getById(id));
    }

    @RequiresPermission("USER_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<UserResponseDto> update(@PathVariable Long id, @RequestBody UserUpdateRequestDto dto) {
        return ResponseBuilder.success("User updated successfully", userService.update(id, dto));
    }

    @RequiresPermission("USER_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<UserResponseDto> deactivate(@PathVariable Long id) {
        return ResponseBuilder.success("User archived successfully", userService.deactivate(id));
    }

    @RequiresPermission("USER_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<UserResponseDto> restore(@PathVariable Long id) {
        return ResponseBuilder.success("User restored successfully", userService.restore(id));
    }

    @RequiresPermission("USER_LOCK")
    @PostMapping("/{id}/lock")
    public ApiResponse<UserResponseDto> lock(@PathVariable Long id,
            @RequestBody(required = false) UserLockActionDto dto) {
        UserResponseDto response = userService.lock(id, dto);
        return ResponseBuilder.success("User locked successfully", response);
    }

    @RequiresPermission("USER_UNLOCK")
    @PostMapping("/{id}/unlock")
    public ApiResponse<UserResponseDto> unlock(@PathVariable Long id,
            @RequestBody(required = false) UserLockActionDto dto) {
        UserResponseDto response = userService.unlock(id, dto);
        return ResponseBuilder.success("User unlocked successfully", response);
    }

    @RequiresPermission("USER_RESET_PASSWORD")
    @PostMapping("/{id}/reset-password")
    public ApiResponse<UserResponseDto> resetPassword(@PathVariable Long id,
            @RequestBody UserPasswordResetDto dto) {
        UserResponseDto response = userService.resetPassword(id, dto);
        return ResponseBuilder.success("User password reset successfully", response);
    }

    @RequiresPermission("USER_ASSIGN_ROLE")
    @PostMapping("/{id}/assign-role")
    public ApiResponse<UserResponseDto> assignRole(@PathVariable Long id,
            @RequestBody UserRoleAssignDto dto) {
        UserResponseDto response = userService.assignRole(id, dto);
        return ResponseBuilder.success("User role assigned successfully", response);
    }

    @RequiresPermission("USER_VIEW")
    @GetMapping("/{id}/history")
    public ApiResponse<List<UserHistoryEntryDto>> history(@PathVariable Long id) {
        return ResponseBuilder.success("User history loaded successfully", userService.getHistory(id));
    }
}
