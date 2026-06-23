package com.sbms.branch.controller.impl;

import com.sbms.branch.controller.IBranchUserAssignmentController;
import com.sbms.branch.dto.BranchUserAssignmentRequestDto;
import com.sbms.branch.dto.BranchUserAssignmentResponseDto;
import com.sbms.branch.service.IBranchUserAssignmentService;
import com.sbms.common.response.ApiResponse;
import com.sbms.config.RequiresPermission;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches/assignments")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("BRANCH_MANAGEMENT_ACCESS")
public class BranchUserAssignmentController implements IBranchUserAssignmentController {

    @Autowired
    private IBranchUserAssignmentService assignmentService;

    @Override
    @RequiresPermission("BRANCH_ASSIGN_USER")
    @PostMapping({"/create", "/assign-user"})
    public ApiResponse<BranchUserAssignmentResponseDto> create(
            @Valid @RequestBody BranchUserAssignmentRequestDto request
    ) {
        return ApiResponse.success("Branch user assigned successfully", assignmentService.create(request));
    }

    @Override
    @RequiresPermission("BRANCH_ASSIGN_USER")
    @PutMapping("/{id}")
    public ApiResponse<BranchUserAssignmentResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody BranchUserAssignmentRequestDto request
    ) {
        return ApiResponse.success("Branch user assignment updated successfully", assignmentService.update(id, request));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<BranchUserAssignmentResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success("Branch user assignment loaded successfully", assignmentService.getById(id));
    }

    @Override
    @GetMapping({"", "/list"})
    public ApiResponse<List<BranchUserAssignmentResponseDto>> getAll(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success("Branch user assignment list loaded successfully",
                assignmentService.getAll(branchId, userId, status));
    }

    @Override
    @RequiresPermission("BRANCH_ASSIGN_USER")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id) {
        assignmentService.deactivate(id);
        return ApiResponse.success("Branch user assignment deactivated successfully", null);
    }
}
