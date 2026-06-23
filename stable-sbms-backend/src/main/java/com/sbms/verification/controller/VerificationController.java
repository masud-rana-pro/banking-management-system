package com.sbms.verification.controller;

import com.sbms.auth.AuthService;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.verification.dto.request.ProviderTestRequest;
import com.sbms.verification.dto.request.StepUpChallengeRequest;
import com.sbms.verification.dto.request.StepUpChallengeVerifyRequest;
import com.sbms.verification.dto.request.VerificationOtpVerifyRequest;
import com.sbms.verification.dto.request.VerificationSendOtpRequest;
import com.sbms.verification.dto.response.StepUpChallengeResponse;
import com.sbms.verification.dto.response.StepUpVerifyResponse;
import com.sbms.verification.dto.response.VerificationChannelResponse;
import com.sbms.verification.dto.response.VerificationContactStatusResponse;
import com.sbms.verification.dto.response.VerificationDashboardSummaryResponse;
import com.sbms.verification.dto.response.VerificationLogResponse;
import com.sbms.verification.dto.response.VerificationTemplateResponse;
import com.sbms.verification.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/verifications")
@RequiresPermission("VERIFICATION_ACCESS")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/dashboard-summary")
    public ApiResponse<VerificationDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Verification dashboard summary fetched successfully", verificationService.dashboardSummary());
    }

    @GetMapping("/logs")
    public ApiResponse<List<VerificationLogResponse>> logs(
            @RequestParam(required = false) String channelType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Verification logs fetched successfully", verificationService.getLogs(channelType, status, keyword));
    }

    @GetMapping("/channels")
    public ApiResponse<List<VerificationChannelResponse>> channels() {
        return ResponseBuilder.success("Verification channels fetched successfully", verificationService.getChannels());
    }

    @GetMapping("/templates")
    public ApiResponse<List<VerificationTemplateResponse>> templates() {
        return ResponseBuilder.success("Verification templates fetched successfully", verificationService.getTemplates());
    }

    @GetMapping("/contact-status")
    public ApiResponse<List<VerificationContactStatusResponse>> contactStatuses(
            @RequestParam(required = false) String referenceModule,
            @RequestParam(required = false) Long referenceId
    ) {
        return ResponseBuilder.success("Contact verification statuses fetched successfully", verificationService.getContactStatuses(referenceModule, referenceId));
    }

    @RequiresPermission("VERIFICATION_SEND_EMAIL_OTP")
    @PostMapping("/send-email-verification-otp")
    public ApiResponse<VerificationLogResponse> sendEmailVerificationOtp(@RequestBody VerificationSendOtpRequest request) {
        return ResponseBuilder.success("Email verification OTP sent successfully", verificationService.sendEmailVerificationOtp(request));
    }

    @RequiresPermission("VERIFICATION_SEND_MOBILE_OTP")
    @PostMapping("/send-mobile-verification-otp")
    public ApiResponse<VerificationLogResponse> sendMobileVerificationOtp(@RequestBody VerificationSendOtpRequest request) {
        return ResponseBuilder.success("Mobile verification OTP sent successfully", verificationService.sendMobileVerificationOtp(request));
    }

    @RequiresPermission("VERIFICATION_VERIFY_OTP")
    @PostMapping("/verify-otp")
    public ApiResponse<VerificationLogResponse> verifyOtp(@RequestBody VerificationOtpVerifyRequest request) {
        return ResponseBuilder.success("OTP verified successfully", verificationService.verifyOtp(request));
    }

    @RequiresPermission("VERIFICATION_RESEND_OTP")
    @PostMapping("/resend-otp/{requestId}")
    public ApiResponse<VerificationLogResponse> resendOtp(@PathVariable Long requestId) {
        return ResponseBuilder.success("OTP resent successfully", verificationService.resendOtp(requestId));
    }

    @RequiresPermission("VERIFICATION_EXPIRE_OTP")
    @PostMapping("/expire-otp/{requestId}")
    public ApiResponse<VerificationLogResponse> expireOtp(@PathVariable Long requestId) {
        return ResponseBuilder.success("OTP expired successfully", verificationService.expireOtp(requestId));
    }

    @RequiresPermission("VERIFICATION_MARK_FAILED")
    @PostMapping("/mark-failed/{requestId}")
    public ApiResponse<VerificationLogResponse> markFailed(@PathVariable Long requestId) {
        return ResponseBuilder.success("OTP request marked failed successfully", verificationService.markFailed(requestId));
    }

    @RequiresPermission("VERIFICATION_PROVIDER_TEST")
    @PostMapping("/provider-test")
    public ApiResponse<VerificationLogResponse> providerTest(@RequestBody ProviderTestRequest request) {
        return ResponseBuilder.success("Provider test OTP sent successfully", verificationService.providerTest(request));
    }

    @PostMapping("/step-up/request")
    public ApiResponse<StepUpChallengeResponse> requestStepUp(@RequestBody StepUpChallengeRequest request,
                                                              HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute(AuthService.REQUEST_USER_ID);
        String username = (String) servletRequest.getAttribute(AuthService.REQUEST_USERNAME);
        return ResponseBuilder.success("Step-up OTP sent successfully",
                verificationService.issueStepUpChallenge(userId, username, request));
    }

    @PostMapping("/step-up/resend/{requestId}")
    public ApiResponse<StepUpChallengeResponse> resendStepUp(@PathVariable Long requestId,
                                                             HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute(AuthService.REQUEST_USER_ID);
        return ResponseBuilder.success("Step-up OTP resent successfully",
                verificationService.resendStepUpChallenge(requestId, userId));
    }

    @PostMapping("/step-up/verify")
    public ApiResponse<StepUpVerifyResponse> verifyStepUp(@RequestBody StepUpChallengeVerifyRequest request,
                                                          HttpServletRequest servletRequest) {
        Long userId = (Long) servletRequest.getAttribute(AuthService.REQUEST_USER_ID);
        String username = (String) servletRequest.getAttribute(AuthService.REQUEST_USERNAME);
        return ResponseBuilder.success("Step-up verification completed successfully",
                verificationService.verifyStepUpChallenge(userId, username, request,
                        servletRequest.getRemoteAddr(), servletRequest.getHeader("User-Agent")));
    }
}
