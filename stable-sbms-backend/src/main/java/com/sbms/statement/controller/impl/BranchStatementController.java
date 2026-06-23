package com.sbms.statement.controller.impl;

import com.sbms.common.aop.AopRequestContext;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.statement.controller.IBranchStatementController;
import com.sbms.statement.dto.request.BranchStatementRequestDto;
import com.sbms.statement.dto.response.BranchStatementRequestResponse;
import com.sbms.statement.service.IStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branch-statements")
@RequiresPermission("STATEMENTS_ACCESS")
public class BranchStatementController implements IBranchStatementController {

    @Autowired
    private IStatementService statementService;

    @Override
    @RequiresPermission("STATEMENT_BRANCH_REQUEST")
    @PostMapping("/request")
    public ApiResponse<BranchStatementRequestResponse> request(@RequestBody BranchStatementRequestDto request) {
        return ResponseBuilder.success("Branch statement request created successfully", statementService.requestBranchStatement(request, actor("SYSTEM")));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<BranchStatementRequestResponse>> list() {
        return ResponseBuilder.success("Branch statement request list fetched successfully", statementService.listBranchStatements());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<BranchStatementRequestResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Branch statement request fetched successfully", statementService.getBranchStatement(id));
    }

    @Override
    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> preview(@PathVariable Long id) {
        return statementService.previewBranchStatement(id);
    }

    @Override
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        return statementService.downloadBranchStatement(id);
    }

    @Override
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        return statementService.exportBranchStatements(exportType, search, status);
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
