package com.sbms.auth;

import com.sbms.auth.dto.AuthChangePasswordRequest;
import com.sbms.auth.dto.AuthLoginRequest;
import com.sbms.auth.dto.AuthLoginOtpVerifyRequest;
import com.sbms.auth.dto.AuthLoginResponse;
import com.sbms.auth.dto.AuthSessionResponse;
import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthLoginResponse> login(@RequestBody AuthLoginRequest request,
                                                HttpServletRequest servletRequest) {
        return ResponseBuilder.success("Login OTP sent successfully", authService.login(request, servletRequest));
    }

    @PostMapping("/verify-login-otp")
    public ApiResponse<AuthLoginResponse> verifyLoginOtp(@RequestBody AuthLoginOtpVerifyRequest request,
                                                         HttpServletRequest servletRequest) {
        return ResponseBuilder.success("Login successful", authService.verifyLoginOtp(request, servletRequest));
    }

    @PostMapping("/resend-login-otp/{requestId}")
    public ApiResponse<AuthLoginResponse> resendLoginOtp(@PathVariable Long requestId) {
        return ResponseBuilder.success("Login OTP resent successfully", authService.resendLoginOtp(requestId));
    }

    @GetMapping("/me")
    public ApiResponse<AuthSessionResponse> me(HttpServletRequest servletRequest) {
        return ResponseBuilder.success("Authenticated user loaded", authService.me(readBearerToken(servletRequest)));
    }

    @GetMapping("/online-users")
    public ApiResponse<java.util.List<String>> onlineUsers(HttpServletRequest servletRequest) {
        return ResponseBuilder.success("Online users loaded", authService.getOnlineUsernames(readBearerToken(servletRequest)));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest servletRequest) {
        authService.logout(readBearerToken(servletRequest));
        return ResponseBuilder.success("Logged out successfully", null);
    }

    @PostMapping("/change-password")
    public ApiResponse<AuthSessionResponse> changePassword(@RequestBody AuthChangePasswordRequest request,
                                                           HttpServletRequest servletRequest) {
        return ResponseBuilder.success("Password updated successfully", authService.changePassword(readBearerToken(servletRequest), request));
    }

    private String readBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}
