package com.sbms.depositscheme.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.depositscheme.controller.IDepositSchemeController;
import com.sbms.depositscheme.dto.request.DepositSchemeEnrollmentRequest;
import com.sbms.depositscheme.dto.request.DepositSchemeRequest;
import com.sbms.depositscheme.dto.response.DepositSchemeDashboardSummaryResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeEnrollmentResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeProfitDistributionResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeResponse;
import com.sbms.depositscheme.dto.response.DepositSchemeScheduleResponse;
import com.sbms.depositscheme.service.IDepositSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deposit-schemes")
@RequiresPermission("DEPOSIT_SCHEMES_ACCESS")
public class DepositSchemeController implements IDepositSchemeController {

    @Autowired
    private IDepositSchemeService depositSchemeService;

    @Override
    @RequiresPermission("DEPOSIT_SCHEME_CREATE")
    @PostMapping("/create")
    public ApiResponse<DepositSchemeResponse> create(@RequestBody DepositSchemeRequest request) {
        return ResponseBuilder.success("Deposit scheme created successfully", depositSchemeService.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<DepositSchemeResponse>> list() {
        return ResponseBuilder.success("Deposit scheme list fetched successfully", depositSchemeService.list());
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<DepositSchemeResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Deposit scheme fetched successfully", depositSchemeService.getById(id));
    }

    @Override
    @RequiresPermission("DEPOSIT_SCHEME_EDIT")
    @PutMapping("/{id}")
    public ApiResponse<DepositSchemeResponse> update(@PathVariable Long id, @RequestBody DepositSchemeRequest request) {
        return ResponseBuilder.success("Deposit scheme updated successfully", depositSchemeService.update(id, request));
    }

    @Override
    @RequiresPermission("DEPOSIT_SCHEME_ARCHIVE")
    @DeleteMapping("/{id}")
    public ApiResponse<DepositSchemeResponse> archive(@PathVariable Long id) {
        return ResponseBuilder.success("Deposit scheme archived successfully", depositSchemeService.archive(id));
    }

    @Override
    @RequiresPermission("DEPOSIT_SCHEME_RESTORE")
    @PutMapping("/{id}/restore")
    public ApiResponse<DepositSchemeResponse> restore(@PathVariable Long id) {
        return ResponseBuilder.success("Deposit scheme restored successfully", depositSchemeService.restore(id));
    }

    @Override
    @RequiresPermission("DEPOSIT_SCHEME_ENROLLMENT_CREATE")
    @PostMapping("/enrollment/create")
    public ApiResponse<DepositSchemeEnrollmentResponse> createEnrollment(@RequestBody DepositSchemeEnrollmentRequest request) {
        return ResponseBuilder.success("Deposit scheme enrollment created successfully", depositSchemeService.createEnrollment(request));
    }

    @Override
    @GetMapping("/enrollment/list")
    public ApiResponse<List<DepositSchemeEnrollmentResponse>> listEnrollments(
            @RequestParam(required = false) Long schemeId,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long accountId
    ) {
        return ResponseBuilder.success(
                "Deposit scheme enrollment list fetched successfully",
                depositSchemeService.listEnrollments(schemeId, customerId, accountId)
        );
    }

    @Override
    @GetMapping("/enrollment/{id}")
    public ApiResponse<DepositSchemeEnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        return ResponseBuilder.success("Deposit scheme enrollment fetched successfully", depositSchemeService.getEnrollmentById(id));
    }

    @Override
    @GetMapping("/enrollment/{id}/certificate/preview")
    public ResponseEntity<byte[]> previewEnrollmentCertificate(@PathVariable Long id) {
        return depositSchemeService.previewEnrollmentCertificate(id);
    }

    @Override
    @GetMapping("/enrollment/{id}/certificate/download")
    public ResponseEntity<byte[]> downloadEnrollmentCertificate(@PathVariable Long id) {
        return depositSchemeService.downloadEnrollmentCertificate(id);
    }

    @Override
    @GetMapping("/enrollment/{id}/schedule")
    public ApiResponse<List<DepositSchemeScheduleResponse>> getEnrollmentSchedule(@PathVariable Long id) {
        return ResponseBuilder.success("Deposit schedule fetched successfully", depositSchemeService.getEnrollmentSchedule(id));
    }

    @Override
    @GetMapping("/enrollment/{id}/profit")
    public ApiResponse<List<DepositSchemeProfitDistributionResponse>> getEnrollmentProfitDistribution(@PathVariable Long id) {
        return ResponseBuilder.success("Profit distribution fetched successfully", depositSchemeService.getEnrollmentProfitDistribution(id));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<DepositSchemeDashboardSummaryResponse> getDashboardSummary() {
        return ResponseBuilder.success("Deposit scheme dashboard fetched successfully", depositSchemeService.getDashboardSummary());
    }
}
