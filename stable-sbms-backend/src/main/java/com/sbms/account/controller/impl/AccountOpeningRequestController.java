package com.sbms.account.controller.impl;

import com.sbms.account.controller.IAccountOpeningRequestController;
import com.sbms.account.dto.request.AccountOpeningRequestDto;
import com.sbms.account.dto.request.AccountWorkflowActionRequest;
import com.sbms.account.dto.response.AccountOpeningRequestResponse;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.account.service.IAccountOpeningRequestService;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-opening-requests")
@RequiresPermission("ACCOUNT_MANAGEMENT_ACCESS")
public class AccountOpeningRequestController implements IAccountOpeningRequestController {

    @Autowired
    private IAccountOpeningRequestService accountOpeningRequestService;

    @Override
    @RequiresPermission("ACCOUNT_REQUEST_CREATE")
    @PostMapping("/create")
    public ApiResponse<AccountOpeningRequestResponse> create(@RequestBody AccountOpeningRequestDto request) {
        return ResponseBuilder.success("Account opening request created successfully", accountOpeningRequestService.create(request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<AccountOpeningRequestResponse>> list() {
        return ResponseBuilder.success("Account opening request list fetched successfully", accountOpeningRequestService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<AccountOpeningRequestResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Account opening request fetched successfully", accountOpeningRequestService.getById(id));
    }

    @Override
    @RequiresPermission("ACCOUNT_REQUEST_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<AccountOpeningRequestResponse> update(@PathVariable Long id, @RequestBody AccountOpeningRequestDto request) {
        return ResponseBuilder.success("Account opening request updated successfully", accountOpeningRequestService.update(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("ACCOUNT_REQUEST_SUBMIT")
    @PostMapping("/{id}/submit")
    public ApiResponse<AccountOpeningRequestResponse> submit(@PathVariable Long id) {
        return ResponseBuilder.success("Account opening request submitted successfully", accountOpeningRequestService.submit(id, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("ACCOUNT_REQUEST_VERIFY")
    @PostMapping("/{id}/verify")
    public ApiResponse<AccountOpeningRequestResponse> verify(@PathVariable Long id) {
        return ResponseBuilder.success("Account opening request verified successfully", accountOpeningRequestService.verify(id, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("ACCOUNT_REQUEST_APPROVE")
    @PostMapping("/{id}/approve")
    public ApiResponse<AccountOpeningRequestResponse> approve(@PathVariable Long id) {
        return ResponseBuilder.success("Account opening request approved successfully", accountOpeningRequestService.approve(id, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("ACCOUNT_REQUEST_REJECT")
    @PostMapping("/{id}/reject")
    public ApiResponse<AccountOpeningRequestResponse> reject(@PathVariable Long id, @RequestBody(required = false) AccountWorkflowActionRequest request) {
        return ResponseBuilder.success("Account opening request rejected successfully", accountOpeningRequestService.reject(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("ACCOUNT_REQUEST_RETURN")
    @PostMapping("/{id}/return")
    public ApiResponse<AccountOpeningRequestResponse> returnForCorrection(@PathVariable Long id, @RequestBody(required = false) AccountWorkflowActionRequest request) {
        return ResponseBuilder.success("Account opening request returned successfully", accountOpeningRequestService.returnForCorrection(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @GetMapping("/{id}/document/preview")
    public ResponseEntity<byte[]> previewOpeningForm(@PathVariable Long id) {
        return accountOpeningRequestService.previewOpeningForm(id);
    }

    @Override
    @GetMapping("/{id}/document/download")
    public ResponseEntity<byte[]> downloadOpeningForm(@PathVariable Long id) {
        return accountOpeningRequestService.downloadOpeningForm(id);
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
