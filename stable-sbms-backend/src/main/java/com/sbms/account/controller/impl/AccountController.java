package com.sbms.account.controller.impl;

import com.sbms.account.controller.IAccountController;
import com.sbms.account.dto.request.AccountWorkflowActionRequest;
import com.sbms.account.dto.response.AccountDashboardSummaryResponse;
import com.sbms.account.dto.response.AccountResponse;
import com.sbms.account.service.IAccountService;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiresPermission("ACCOUNT_MANAGEMENT_ACCESS")
public class AccountController implements IAccountController {

    @Autowired
    private IAccountService accountService;

    @Override
    @GetMapping("/list")
    public ApiResponse<List<AccountResponse>> list() {
        return ResponseBuilder.success("Account list fetched successfully", accountService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<AccountResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Account fetched successfully", accountService.getById(id));
    }

    @Override
    @GetMapping("/search")
    public ApiResponse<List<AccountResponse>> search(@RequestParam(required = false) String keyword) {
        return ResponseBuilder.success("Account search result fetched successfully", accountService.search(keyword));
    }

    @Override
    @RequiresPermission("ACCOUNT_ACTIVATE")
    @PostMapping("/{id}/activate")
    public ApiResponse<AccountResponse> activate(@PathVariable Long id, @RequestBody(required = false) AccountWorkflowActionRequest request) {
        return ResponseBuilder.success("Account activated successfully", accountService.activate(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("ACCOUNT_BLOCK")
    @PostMapping("/{id}/block")
    public ApiResponse<AccountResponse> block(@PathVariable Long id, @RequestBody(required = false) AccountWorkflowActionRequest request) {
        return ResponseBuilder.success("Account blocked successfully", accountService.block(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("ACCOUNT_FREEZE")
    @PostMapping("/{id}/freeze")
    public ApiResponse<AccountResponse> freeze(@PathVariable Long id, @RequestBody(required = false) AccountWorkflowActionRequest request) {
        return ResponseBuilder.success("Account frozen successfully", accountService.freeze(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("ACCOUNT_CLOSE")
    @PostMapping("/{id}/close")
    public ApiResponse<AccountResponse> close(@PathVariable Long id, @RequestBody(required = false) AccountWorkflowActionRequest request) {
        return ResponseBuilder.success("Account closed successfully", accountService.close(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<AccountDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Account dashboard summary fetched successfully", accountService.dashboardSummary());
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
