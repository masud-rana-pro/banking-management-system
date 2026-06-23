package com.sbms.statement.controller.impl;

import com.sbms.common.aop.AopRequestContext;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.statement.controller.ICustomerStatementController;
import com.sbms.statement.dto.request.CustomerStatementRequestDto;
import com.sbms.statement.dto.response.CustomerStatementRequestResponse;
import com.sbms.statement.service.IStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-statements")
@RequiresPermission("STATEMENTS_ACCESS")
public class CustomerStatementController implements ICustomerStatementController {

    @Autowired
    private IStatementService statementService;

    @Override
    @RequiresPermission("STATEMENT_CUSTOMER_REQUEST")
    @PostMapping("/request")
    public ApiResponse<CustomerStatementRequestResponse> request(@RequestBody CustomerStatementRequestDto request) {
        return ResponseBuilder.success("Customer statement request created successfully", statementService.requestCustomerStatement(request, actor("SYSTEM")));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<CustomerStatementRequestResponse>> list() {
        return ResponseBuilder.success("Customer statement request list fetched successfully", statementService.listCustomerStatements());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<CustomerStatementRequestResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Customer statement request fetched successfully", statementService.getCustomerStatement(id));
    }

    @Override
    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> preview(@PathVariable Long id) {
        return statementService.previewCustomerStatement(id);
    }

    @Override
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        return statementService.downloadCustomerStatement(id);
    }

    @Override
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String exportType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {
        return statementService.exportCustomerStatements(exportType, search, status);
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
