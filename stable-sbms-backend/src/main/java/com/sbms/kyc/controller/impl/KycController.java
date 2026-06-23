package com.sbms.kyc.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.common.aop.AopRequestContext;
import com.sbms.config.RequiresPermission;
import com.sbms.kyc.controller.IKycController;
import com.sbms.kyc.dto.request.KycActionRequest;
import com.sbms.kyc.dto.request.KycDocumentRequest;
import com.sbms.kyc.dto.request.KycProfileRequest;
import com.sbms.kyc.dto.response.KycDashboardSummaryResponse;
import com.sbms.kyc.dto.response.KycDecisionHistoryResponse;
import com.sbms.kyc.dto.response.KycDocumentResponse;
import com.sbms.kyc.dto.response.KycProfileResponse;
import com.sbms.kyc.service.IKycService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kyc")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("KYC_MANAGEMENT_ACCESS")
public class KycController implements IKycController {

    @Autowired
    private IKycService kycService;

    @Override
    @RequiresPermission("KYC_CREATE")
    @PostMapping("/profile/create")
    public ApiResponse<KycProfileResponse> createProfile(@RequestBody KycProfileRequest request) {
        return ResponseBuilder.success("KYC profile created successfully", kycService.createProfile(request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @GetMapping("/profile/list")
    public ApiResponse<List<KycProfileResponse>> listProfiles() {
        return ResponseBuilder.success("KYC profile list fetched successfully", kycService.getProfiles());
    }

    @Override
    @GetMapping("/profile/customer/{customerId}")
    public ApiResponse<KycProfileResponse> getProfileByCustomerId(@PathVariable Long customerId) {
        return ResponseBuilder.success("KYC profile fetched successfully", kycService.getProfileByCustomerId(customerId));
    }

    @Override
    @GetMapping("/profile/{id}")
    public ApiResponse<KycProfileResponse> getProfileById(@PathVariable Long id) {
        return ResponseBuilder.success("KYC profile fetched successfully", kycService.getProfileById(id));
    }

    @Override
    @RequiresPermission("KYC_EDIT")
    @PutMapping("/profile/{id}")
    public ApiResponse<KycProfileResponse> updateProfile(@PathVariable Long id, @RequestBody KycProfileRequest request) {
        return ResponseBuilder.success("KYC profile updated successfully", kycService.updateProfile(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("KYC_SUBMIT")
    @PostMapping("/profile/{id}/submit")
    public ApiResponse<KycProfileResponse> submitProfile(@PathVariable Long id) {
        return ResponseBuilder.success("KYC profile submitted successfully", kycService.submitProfile(id, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("KYC_VERIFY")
    @PostMapping("/profile/{id}/verify")
    public ApiResponse<KycProfileResponse> verifyProfile(@PathVariable Long id) {
        return ResponseBuilder.success("KYC profile verified successfully", kycService.verifyProfile(id, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("KYC_APPROVE")
    @PostMapping("/profile/{id}/approve")
    public ApiResponse<KycProfileResponse> approveProfile(@PathVariable Long id) {
        return ResponseBuilder.success("KYC profile approved successfully", kycService.approveProfile(id, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("KYC_REJECT")
    @PostMapping("/profile/{id}/reject")
    public ApiResponse<KycProfileResponse> rejectProfile(@PathVariable Long id, @RequestBody(required = false) KycActionRequest request) {
        return ResponseBuilder.success("KYC profile rejected successfully", kycService.rejectProfile(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("KYC_RETURN")
    @PostMapping("/profile/{id}/return")
    public ApiResponse<KycProfileResponse> returnProfile(@PathVariable Long id, @RequestBody(required = false) KycActionRequest request) {
        return ResponseBuilder.success("KYC profile returned successfully", kycService.returnProfile(id, request, actor("SYSTEM_REVIEWER")));
    }

    @Override
    @RequiresPermission("KYC_DOCUMENT_UPLOAD")
    @PostMapping("/document/upload")
    public ApiResponse<KycDocumentResponse> uploadDocument(@RequestBody KycDocumentRequest request) {
        return ResponseBuilder.success("KYC document uploaded successfully", kycService.uploadDocument(request));
    }

    @Override
    @GetMapping("/profile/{id}/documents")
    public ApiResponse<List<KycDocumentResponse>> getDocumentsByProfile(@PathVariable Long id) {
        return ResponseBuilder.success("KYC documents fetched successfully", kycService.getDocumentsByProfile(id));
    }

    @Override
    @GetMapping("/decision-history/{kycId}")
    public ApiResponse<List<KycDecisionHistoryResponse>> getDecisionHistory(@PathVariable Long kycId) {
        return ResponseBuilder.success("KYC decision history fetched successfully", kycService.getDecisionHistory(kycId));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<KycDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("KYC dashboard summary fetched successfully", kycService.dashboardSummary());
    }

    private String actor(String fallback) {
        String username = AopRequestContext.currentUsername();
        return username == null || username.trim().isEmpty() ? fallback : username.trim();
    }
}
