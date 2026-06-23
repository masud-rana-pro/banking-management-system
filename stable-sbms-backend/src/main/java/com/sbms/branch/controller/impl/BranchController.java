package com.sbms.branch.controller.impl;

import com.sbms.branch.controller.IBranchController;
import com.sbms.branch.dto.BranchRequestDto;
import com.sbms.branch.dto.BranchResponseDto;
import com.sbms.branch.service.IBranchService;
import com.sbms.common.response.ApiResponse;
import com.sbms.config.RequiresPermission;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sbms.branch.dto.BranchDashboardSummaryDto;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("BRANCH_MANAGEMENT_ACCESS")
public class BranchController implements IBranchController {

    @Autowired
    private IBranchService branchService;

    @Override
    @RequiresPermission("BRANCH_CREATE")
    @PostMapping("/create")
    public ApiResponse<BranchResponseDto> create(@Valid @RequestBody BranchRequestDto request) {
        return ApiResponse.success("Branch created successfully", branchService.create(request));
    }

    @Override
    @RequiresPermission("BRANCH_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<BranchResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody BranchRequestDto request
    ) {
        return ApiResponse.success("Branch updated successfully", branchService.update(id, request));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<BranchResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success("Branch loaded successfully", branchService.getById(id));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<BranchResponseDto>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success("Branch list loaded successfully", branchService.getAll(search, status));
    }

    @Override
    @GetMapping("/dropdown")
    public ApiResponse<List<BranchResponseDto>> dropdown() {
        return ApiResponse.success("Branch dropdown loaded successfully", branchService.dropdown());
    }

    @Override
    @RequiresPermission("BRANCH_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> softDelete(@PathVariable Long id) {
        branchService.softDelete(id);
        return ApiResponse.success("Branch archived successfully", null);
    }

    @Override
    @RequiresPermission("BRANCH_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<Void> restore(@PathVariable Long id) {
        branchService.restore(id);
        return ApiResponse.success("Branch restored successfully", null);
    }
    
    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<BranchDashboardSummaryDto> getDashboardSummary() {
        return ApiResponse.success(
                "Branch dashboard summary loaded successfully",
                branchService.getDashboardSummary()
        );
    }
}
