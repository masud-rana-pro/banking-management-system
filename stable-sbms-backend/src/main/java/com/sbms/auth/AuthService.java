package com.sbms.auth;

import com.sbms.auth.dto.AuthChangePasswordRequest;
import com.sbms.auth.dto.AuthLoginRequest;
import com.sbms.auth.dto.AuthLoginOtpVerifyRequest;
import com.sbms.auth.dto.AuthLoginResponse;
import com.sbms.auth.dto.AuthSessionResponse;
import com.sbms.branch.repository.BranchRepository;
import com.sbms.common.websocket.LiveUpdateSessionRegistry;
import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.UnauthorizedException;
import com.sbms.role.entity.RolePermission;
import com.sbms.role.repository.RolePermissionRepository;
import com.sbms.user.entity.User;
import com.sbms.user.entity.UserSession;
import com.sbms.user.enums.UserStatus;
import com.sbms.user.repository.UserRepository;
import com.sbms.user.repository.UserSessionRepository;
import com.sbms.verification.entity.OtpVerificationRequest;
import com.sbms.verification.service.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Transactional
public class AuthService {

    public static final String REQUEST_USER_ID = "SBMS_AUTH_USER_ID";
    public static final String REQUEST_USERNAME = "SBMS_AUTH_USERNAME";
    public static final String REQUEST_ROLE_CODE = "SBMS_AUTH_ROLE_CODE";
    public static final String REQUEST_PERMISSIONS = "SBMS_AUTH_PERMISSIONS";

    private static final int MAX_FAILED_LOGINS = 5;
    private static final int SESSION_EXPIRY_HOURS = 12;
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[aby]\\$.{56}$");

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final BranchRepository branchRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final LiveUpdateSessionRegistry liveUpdateSessionRegistry;

    public AuthService(UserRepository userRepository,
                       UserSessionRepository userSessionRepository,
                       RolePermissionRepository rolePermissionRepository,
                       BranchRepository branchRepository,
                       PasswordEncoder passwordEncoder,
                       VerificationService verificationService,
                       LiveUpdateSessionRegistry liveUpdateSessionRegistry) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.branchRepository = branchRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
        this.liveUpdateSessionRegistry = liveUpdateSessionRegistry;
    }

    public AuthLoginResponse login(AuthLoginRequest request, HttpServletRequest servletRequest) {
        if (request == null || isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            throw new BadRequestException("Username and password are required");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!Boolean.TRUE.equals(user.getActive()) || user.getStatus() == UserStatus.INACTIVE) {
            throw new UnauthorizedException("User account is inactive");
        }
        if (Boolean.TRUE.equals(user.getLocked()) || user.getStatus() == UserStatus.LOCKED) {
            throw new UnauthorizedException("User account is locked");
        }

        boolean legacyHash = isLegacySha256(user.getPasswordHash());
        boolean passwordMatched = legacyHash
                ? matchesLegacySha256(request.getPassword(), user.getPasswordHash())
                : passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if (!passwordMatched) {
            registerFailedLogin(user, servletRequest);
            throw new UnauthorizedException("Invalid username or password");
        }

        if (legacyHash) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        OtpVerificationRequest otpRequest = verificationService.issueLoginOtp(
                user,
                "Login OTP issued after credential validation",
                request.getOtpChannelPreference()
        );
        return toOtpChallengeResponse(otpRequest);
    }

    public AuthLoginResponse verifyLoginOtp(AuthLoginOtpVerifyRequest request, HttpServletRequest servletRequest) {
        if (request == null || request.getRequestId() == null || isBlank(request.getOtpCode())) {
            throw new BadRequestException("OTP request id and code are required");
        }
        OtpVerificationRequest otpRequest = verificationService.verifyLoginOtp(
                request.getRequestId(),
                request.getOtpCode(),
                clientIp(servletRequest),
                clientDevice(servletRequest),
                "AUTH_LOGIN"
        );
        User user = otpRequest.getUser();
        if (user == null) {
            throw new UnauthorizedException("Login OTP request was not linked to a user");
        }

        user.setFailedLoginCount(0);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLocked(false);
        user.setLockedAt(null);
        user.setUpdatedBy(user.getUsername());
        userRepository.save(user);

        UserSession session = new UserSession();
        session.setUser(user);
        session.setJwtId(UUID.randomUUID().toString() + "-" + UUID.randomUUID());
        session.setIpAddress(clientIp(servletRequest));
        session.setDeviceInfo(clientDevice(servletRequest));
        session.setStatus("SUCCESS");
        userSessionRepository.save(session);

        AuthLoginResponse response = new AuthLoginResponse();
        response.setOtpRequired(false);
        response.setSession(toSessionResponse(user, session));
        return response;
    }

    public AuthLoginResponse resendLoginOtp(Long requestId) {
        OtpVerificationRequest otpRequest = verificationService.resendLoginOtp(requestId);
        return toOtpChallengeResponse(otpRequest);
    }

    @Transactional(readOnly = true)
    public AuthSessionResponse me(String token) {
        UserSession session = resolveActiveSession(token);
        return toSessionResponse(session.getUser(), session);
    }

    public void logout(String token) {
        UserSession session = resolveActiveSession(token);
        session.setLogoutTime(LocalDateTime.now());
        session.setStatus("LOGOUT");
        userSessionRepository.save(session);
    }

    public AuthSessionResponse changePassword(String token, AuthChangePasswordRequest request) {
        if (request == null || isBlank(request.getCurrentPassword()) || isBlank(request.getNewPassword())) {
            throw new BadRequestException("Current password and new password are required");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password confirmation does not match");
        }
        if (request.getNewPassword().trim().length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters");
        }

        UserSession session = resolveActiveSession(token);
        User user = session.getUser();
        boolean matched = isLegacySha256(user.getPasswordHash())
                ? matchesLegacySha256(request.getCurrentPassword(), user.getPasswordHash())
                : passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash());
        if (!matched) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setMustChangePassword(false);
        user.setUpdatedBy(user.getUsername());
        userRepository.save(user);

        return toSessionResponse(user, session);
    }

    @Transactional(readOnly = true)
    public List<String> getOnlineUsernames(String token) {
        resolveActiveSession(token);
        return liveUpdateSessionRegistry.onlineUsernames().stream()
                .sorted(String::compareToIgnoreCase)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserSession resolveActiveSession(String token) {
        if (isBlank(token)) {
            throw new UnauthorizedException("Missing bearer token");
        }
        UserSession session = userSessionRepository.findActiveByToken(token.trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired session"));
        if (session.getLoginTime() != null && session.getLoginTime().isBefore(LocalDateTime.now().minusHours(SESSION_EXPIRY_HOURS))) {
            throw new UnauthorizedException("Session expired. Please sign in again.");
        }
        return session;
    }

    private void registerFailedLogin(User user, HttpServletRequest servletRequest) {
        int failedCount = (user.getFailedLoginCount() == null ? 0 : user.getFailedLoginCount()) + 1;
        user.setFailedLoginCount(failedCount);
        user.setUpdatedBy(user.getUsername());
        if (failedCount >= MAX_FAILED_LOGINS) {
            user.setLocked(true);
            user.setActive(false);
            user.setStatus(UserStatus.LOCKED);
            user.setLockedAt(LocalDateTime.now());
        }
        userRepository.save(user);

        UserSession failedAttempt = new UserSession();
        failedAttempt.setUser(user);
        failedAttempt.setIpAddress(clientIp(servletRequest));
        failedAttempt.setDeviceInfo(clientDevice(servletRequest));
        failedAttempt.setStatus("FAILED");
        userSessionRepository.save(failedAttempt);
    }

    private AuthSessionResponse toSessionResponse(User user, UserSession session) {
        AuthSessionResponse response = new AuthSessionResponse();
        response.setToken(session.getJwtId());
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setProfileImageName(user.getProfileImageName());
        response.setMustChangePassword(Boolean.TRUE.equals(user.getMustChangePassword()));
        response.setLoginAt(session.getLoginTime());
        response.setBranchId(user.getBranchId());
        if (user.getBranchId() != null) {
            branchRepository.findById(user.getBranchId()).ifPresent(branch -> response.setBranchName(branch.getBranchName()));
        }
        if (user.getRole() != null) {
            response.setRoleId(user.getRole().getId());
            response.setRoleCode(user.getRole().getCode());
            response.setRoleName(user.getRole().getName());
            response.setPermissions(getGrantedPermissions(user));
        } else {
            response.setPermissions(List.of());
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<String> getGrantedPermissions(User user) {
        if (user == null || user.getRole() == null || user.getRole().getId() == null) {
            return List.of();
        }
        return rolePermissionRepository.findByRoleId(user.getRole().getId()).stream()
                .filter(RolePermission::getAllowFlag)
                .map(RolePermission::getPermissionCode)
                .distinct()
                .sorted()
                .toList();
    }

    private boolean isLegacySha256(String value) {
        return value != null && value.length() == 64 && value.chars().allMatch(ch ->
                (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'));
    }

    private boolean matchesLegacySha256(String raw, String encoded) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes).equalsIgnoreCase(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to validate password", e);
        }
    }

    private String clientIp(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";
        String forwarded = request.getHeader("X-Forwarded-For");
        if (!isBlank(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String clientDevice(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";
        String agent = request.getHeader("User-Agent");
        return isBlank(agent) ? "UNKNOWN" : agent.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private AuthLoginResponse toOtpChallengeResponse(OtpVerificationRequest otpRequest) {
        AuthLoginResponse response = new AuthLoginResponse();
        response.setOtpRequired(true);
        response.setOtpRequestId(otpRequest.getId());
        response.setOtpChannelType(otpRequest.getChannelType().name());
        response.setOtpDestinationMasked(otpRequest.getChannelType().name().equals("EMAIL")
                ? maskEmail(otpRequest.getContactValue())
                : maskMobile(otpRequest.getContactValue()));
        response.setOtpExpiresAt(otpRequest.getExpiresAt());
        return response;
    }


    private String maskEmail(String value) {
        if (isBlank(value) || !value.contains("@")) {
            return value;
        }
        String[] parts = value.split("@", 2);
        String name = parts[0];
        String domain = parts[1];
        String maskedName = name.length() <= 2 ? name.charAt(0) + "*" : name.substring(0, 2) + "***";
        return maskedName + "@" + domain;
    }

    private String maskMobile(String value) {
        if (isBlank(value) || value.length() < 6) {
            return value;
        }
        return value.substring(0, 3) + "****" + value.substring(value.length() - 3);
    }
}



