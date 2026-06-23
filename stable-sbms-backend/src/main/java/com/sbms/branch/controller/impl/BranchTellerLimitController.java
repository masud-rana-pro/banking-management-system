package com.sbms.branch.controller.impl;

import com.sbms.branch.controller.IBranchTellerLimitController;
import com.sbms.branch.dto.BranchTellerLimitRequestDto;
import com.sbms.branch.dto.BranchTellerLimitResponseDto;
import com.sbms.branch.service.IBranchTellerLimitService;
import com.sbms.common.response.ApiResponse;
import com.sbms.config.RequiresPermission;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches/teller-limit")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("BRANCH_MANAGEMENT_ACCESS")
public class BranchTellerLimitController implements IBranchTellerLimitController {

    @Autowired
    private IBranchTellerLimitService tellerLimitService;

    @Override
    @RequiresPermission("BRANCH_TELLER_LIMIT_MANAGE")
    @PostMapping("/create")
    public ApiResponse<BranchTellerLimitResponseDto> create(@Valid @RequestBody BranchTellerLimitRequestDto request) {
        return ApiResponse.success("Teller limit created successfully", tellerLimitService.create(request));
    }

    @Override
    @RequiresPermission("BRANCH_TELLER_LIMIT_MANAGE")
    @PutMapping("/{id}")
    public ApiResponse<BranchTellerLimitResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody BranchTellerLimitRequestDto request
    ) {
        return ApiResponse.success("Teller limit updated successfully", tellerLimitService.update(id, request));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<BranchTellerLimitResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success("Teller limit loaded successfully", tellerLimitService.getById(id));
    }

    @Override
    @GetMapping({"", "/list"})
    public ApiResponse<List<BranchTellerLimitResponseDto>> getAll(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(
                "Teller limit list loaded successfully",
                tellerLimitService.getAll(branchId, userId, status)
        );
    }

    @Override
    @RequiresPermission("BRANCH_TELLER_LIMIT_MANAGE")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivate(@PathVariable Long id) {
        tellerLimitService.deactivate(id);
        return ApiResponse.success("Teller limit deactivated successfully", null);
    }
}
