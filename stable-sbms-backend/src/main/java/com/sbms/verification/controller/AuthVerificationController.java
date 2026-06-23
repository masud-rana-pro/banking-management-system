package com.sbms.verification.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.verification.dto.request.ForgotPasswordRequest;
import com.sbms.verification.dto.request.ProviderTestRequest;
import com.sbms.verification.dto.request.ResetPasswordRequest;
import com.sbms.verification.dto.request.VerificationOtpVerifyRequest;
import com.sbms.verification.dto.response.VerificationLogResponse;
import com.sbms.verification.service.VerificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthVerificationController {

    private final VerificationService verificationService;

    public AuthVerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/send-otp")
    public ApiResponse<VerificationLogResponse> sendOtp(@RequestBody ProviderTestRequest request) {
        return ResponseBuilder.success("OTP sent successfully", verificationService.providerTest(request));
    }

    @PostMapping("/verify-otp")
    public ApiResponse<VerificationLogResponse> verifyOtp(@RequestBody VerificationOtpVerifyRequest request) {
        return ResponseBuilder.success("OTP verified successfully", verificationService.verifyOtp(request));
    }

    @PostMapping("/forgot-password")
    public ApiResponse<VerificationLogResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseBuilder.success("Password reset OTP sent successfully", verificationService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ApiResponse<VerificationLogResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseBuilder.success("Password reset completed successfully", verificationService.resetPassword(request));
    }
}
