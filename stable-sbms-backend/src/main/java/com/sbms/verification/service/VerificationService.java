package com.sbms.verification.service;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ForbiddenException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.common.websocket.LiveUpdateGateway;
import com.sbms.customer.entity.Customer;
import com.sbms.customer.enums.CustomerStatus;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.customer.repository.CustomerRepository;
import com.sbms.user.entity.User;
import com.sbms.user.enums.UserStatus;
import com.sbms.user.repository.UserRepository;
import com.sbms.verification.dto.request.*;
import com.sbms.verification.dto.response.*;
import com.sbms.verification.entity.*;
import com.sbms.verification.enums.*;
import com.sbms.verification.repository.*;
import com.sbms.verification.util.MaskingUtil;
import com.sbms.verification.util.OtpGeneratorUtil;
import com.sbms.verification.util.OtpHashUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class VerificationService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 45;
    private static final int DEFAULT_MAX_ATTEMPTS = 5;
    private static final int STEP_UP_TOKEN_EXPIRY_MINUTES = 10;
    private static final Pattern OTP_PREVIEW_PATTERN = Pattern.compile("otpPreview=([0-9]{4,8})");
    private static final String STEP_UP_REQUEST_HEADER = "X-Step-Up-Token";

    private final VerificationChannelRepository channelRepository;
    private final VerificationTemplateRepository templateRepository;
    private final OtpVerificationRequestRepository requestRepository;
    private final VerificationDispatchQueueRepository queueRepository;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final VerificationAttemptLogRepository attemptLogRepository;
    private final ContactVerificationStatusRepository contactVerificationStatusRepository;
    private final StepUpVerificationChallengeRepository stepUpChallengeRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final VerificationDeliveryService deliveryService;
    private final LiveUpdateGateway liveUpdateGateway;
    private final PasswordEncoder passwordEncoder;
    private final AutomatedMailService automatedMailService;

    public VerificationService(
            VerificationChannelRepository channelRepository,
            VerificationTemplateRepository templateRepository,
            OtpVerificationRequestRepository requestRepository,
            VerificationDispatchQueueRepository queueRepository,
            PasswordResetRequestRepository passwordResetRequestRepository,
            VerificationAttemptLogRepository attemptLogRepository,
            ContactVerificationStatusRepository contactVerificationStatusRepository,
            StepUpVerificationChallengeRepository stepUpChallengeRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            VerificationDeliveryService deliveryService,
            LiveUpdateGateway liveUpdateGateway,
            PasswordEncoder passwordEncoder,
            AutomatedMailService automatedMailService
    ) {
        this.channelRepository = channelRepository;
        this.templateRepository = templateRepository;
        this.requestRepository = requestRepository;
        this.queueRepository = queueRepository;
        this.passwordResetRequestRepository = passwordResetRequestRepository;
        this.attemptLogRepository = attemptLogRepository;
        this.contactVerificationStatusRepository = contactVerificationStatusRepository;
        this.stepUpChallengeRepository = stepUpChallengeRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.deliveryService = deliveryService;
        this.liveUpdateGateway = liveUpdateGateway;
        this.passwordEncoder = passwordEncoder;
        this.automatedMailService = automatedMailService;
    }

    public VerificationDashboardSummaryResponse dashboardSummary() {
        return new VerificationDashboardSummaryResponse(
                requestRepository.countPendingActive(LocalDateTime.now(), List.of(VerificationStatus.PENDING, VerificationStatus.SENT)),
                requestRepository.countByRequestStatus(VerificationStatus.VERIFIED),
                requestRepository.countByRequestStatus(VerificationStatus.FAILED),
                passwordResetRequestRepository.count(),
                queueRepository.count(),
                contactVerificationStatusRepository.countByContactTypeAndIsVerifiedAndStatus("EMAIL", false, RecordStatus.ACTIVE),
                contactVerificationStatusRepository.countByContactTypeAndIsVerifiedAndStatus("MOBILE", false, RecordStatus.ACTIVE),
                requestRepository.findTop8ByOrderByIdDesc().stream().map(this::mapLog).toList(),
                attemptLogRepository.findTop20ByOrderByIdDesc().stream().map(this::mapAttempt).toList(),
                contactVerificationStatusRepository.findTop10ByStatusOrderByUpdatedAtDesc(RecordStatus.ACTIVE).stream().map(this::mapContactStatus).toList()
        );
    }

    public List<VerificationLogResponse> getLogs(String channelType, String status, String keyword) {
        return requestRepository.findAllWithRelations().stream()
                .filter(item -> channelType == null || channelType.isBlank() || item.getChannelType().name().equalsIgnoreCase(channelType))
                .filter(item -> status == null || status.isBlank() || item.getRequestStatus().name().equalsIgnoreCase(status))
                .filter(item -> matchesKeyword(item, keyword))
                .map(this::mapLog)
                .toList();
    }

    public List<VerificationChannelResponse> getChannels() {
        return channelRepository.findByStatusOrderByChannelNameAsc(RecordStatus.ACTIVE).stream()
                .map(item -> new VerificationChannelResponse(
                        item.getId(), item.getChannelCode(), item.getChannelName(), item.getProviderName(),
                        item.getStatus().name(), item.getCreatedAt(), item.getUpdatedAt()))
                .toList();
    }

    public List<VerificationTemplateResponse> getTemplates() {
        return templateRepository.findByStatusOrderByTemplateNameAsc(RecordStatus.ACTIVE).stream()
                .map(item -> new VerificationTemplateResponse(
                        item.getId(), item.getPurpose().name(), item.getChannelType().name(), item.getTemplateCode(),
                        item.getTemplateName(), item.getSubjectLine(), item.getTemplateBody(),
                        item.getStatus().name(), item.getCreatedAt(), item.getUpdatedAt()))
                .toList();
    }

    public List<VerificationContactStatusResponse> getContactStatuses(String referenceModule, Long referenceId) {
        List<ContactVerificationStatus> rows;
        if (!blank(referenceModule) && referenceId != null) {
            rows = contactVerificationStatusRepository.findByReferenceModuleIgnoreCaseAndReferenceIdAndStatusOrderByUpdatedAtDesc(
                    referenceModule.trim(), referenceId, RecordStatus.ACTIVE
            );
        } else {
            rows = contactVerificationStatusRepository.findByStatusOrderByUpdatedAtDesc(RecordStatus.ACTIVE);
        }
        return rows.stream().map(this::mapContactStatus).toList();
    }

    public VerificationLogResponse sendEmailVerificationOtp(VerificationSendOtpRequest request) {
        VerificationLogResponse response = mapLog(sendOtp(resolveContact(request, ChannelType.EMAIL), ChannelType.EMAIL, VerificationPurpose.VERIFY_EMAIL, request.userId(), request.customerId(), request.referenceModule(), request.referenceId(), request.remarks(), TokenType.OTP));
        publishVerificationEvent("Email OTP Sent", response.purpose(), response.referenceModule(), "INFO");
        return response;
    }

    public VerificationLogResponse sendMobileVerificationOtp(VerificationSendOtpRequest request) {
        VerificationLogResponse response = mapLog(sendOtp(resolveContact(request, ChannelType.SMS), ChannelType.SMS, VerificationPurpose.VERIFY_MOBILE, request.userId(), request.customerId(), request.referenceModule(), request.referenceId(), request.remarks(), TokenType.OTP));
        publishVerificationEvent("Mobile OTP Sent", response.purpose(), response.referenceModule(), "INFO");
        return response;
    }

    public VerificationLogResponse providerTest(ProviderTestRequest request) {
        ChannelType channelType = parseChannelType(request.channelType());
        VerificationPurpose purpose = parsePurpose(request.purpose(), VerificationPurpose.PROVIDER_TEST);
        validateContact(request.contactValue(), channelType);
        VerificationLogResponse response = mapLog(sendOtp(request.contactValue(), channelType, purpose, null, null, request.referenceModule(), request.referenceId(), request.remarks(), TokenType.OTP));
        publishVerificationEvent("Provider Test OTP Sent", response.purpose(), response.referenceModule(), "SUCCESS");
        return response;
    }

    public OtpVerificationRequest issueLoginOtp(User user, String remarks, String preferredChannel) {
        if (user == null || user.getId() == null) {
            throw new BadRequestException("User is required for login OTP");
        }
        ContactSelection contactSelection = resolvePreferredLoginContact(user, preferredChannel);
        Optional<OtpVerificationRequest> activeExisting = requestRepository.findActiveForContact(
                        contactSelection.contactValue(), VerificationPurpose.LOGIN_OTP, List.of(VerificationStatus.PENDING, VerificationStatus.SENT), LocalDateTime.now())
                .stream()
                .filter(item -> item.getUser() != null && user.getId().equals(item.getUser().getId()))
                .max(Comparator.comparing(OtpVerificationRequest::getId));
        if (activeExisting.isPresent()) {
            return refreshOtp(activeExisting.get(), blank(remarks) ? "Login OTP reissued after credential validation" : remarks.trim());
        }
        return sendOtp(contactSelection.contactValue(), contactSelection.channelType(), VerificationPurpose.LOGIN_OTP,
                user.getId(), null, "AUTH", user.getId(), blank(remarks) ? "Login OTP issued after credential validation" : remarks.trim(), TokenType.OTP);
    }

    public StepUpChallengeResponse issueStepUpChallenge(Long userId, String username, StepUpChallengeRequest request) {
        if (userId == null) {
            throw new BadRequestException("Authenticated user is required for step-up verification");
        }
        if (request == null || blank(request.actionCode()) || blank(request.targetModule())) {
            throw new BadRequestException("Action code and target module are required");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String actionCode = request.actionCode().trim().toUpperCase(Locale.ROOT);
        String targetModule = request.targetModule().trim().toUpperCase(Locale.ROOT);
        Long targetId = request.targetId();

        Optional<StepUpVerificationChallenge> reusableChallenge = stepUpChallengeRepository
                .findReusableActiveChallenge(userId, actionCode, targetModule, targetId);
        OtpVerificationRequest otpRequest;
        StepUpVerificationChallenge challenge;
        if (reusableChallenge.isPresent()) {
            challenge = reusableChallenge.get();
            otpRequest = refreshOtp(challenge.getRequest(), blank(request.remarks()) ? "Step-up OTP resent for " + actionCode : request.remarks().trim());
            challenge.setRequest(otpRequest);
            stepUpChallengeRepository.save(challenge);
        } else {
            ContactSelection contactSelection = resolvePreferredLoginContact(user, request.channelPreference());
            otpRequest = sendOtp(contactSelection.contactValue(), contactSelection.channelType(), VerificationPurpose.STEP_UP_ACTION,
                    user.getId(), null, targetModule, targetId, blank(request.remarks()) ? "Step-up OTP issued for " + actionCode : request.remarks().trim(), TokenType.OTP);
            challenge = new StepUpVerificationChallenge();
            challenge.setRequest(otpRequest);
            challenge.setUser(user);
            challenge.setActionCode(actionCode);
            challenge.setTargetModule(targetModule);
            challenge.setTargetId(targetId);
            stepUpChallengeRepository.save(challenge);
        }
        return mapStepUpChallenge(challenge, otpRequest);
    }

    public StepUpChallengeResponse resendStepUpChallenge(Long requestId, Long userId) {
        StepUpVerificationChallenge challenge = stepUpChallengeRepository.findTopByRequestIdOrderByIdDesc(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Step-up challenge not found"));
        if (userId != null && (challenge.getUser() == null || !userId.equals(challenge.getUser().getId()))) {
            throw new ForbiddenException("You do not have permission to resend this step-up challenge");
        }
        OtpVerificationRequest otpRequest = refreshOtp(challenge.getRequest(), "Step-up OTP resent");
        challenge.setRequest(otpRequest);
        challenge.setVerificationToken(null);
        challenge.setVerifiedAt(null);
        challenge.setTokenExpiresAt(null);
        challenge.setConsumedAt(null);
        stepUpChallengeRepository.save(challenge);
        return mapStepUpChallenge(challenge, otpRequest);
    }

    public StepUpVerifyResponse verifyStepUpChallenge(Long userId, String username, StepUpChallengeVerifyRequest request,
                                                      String ipAddress, String deviceInfo) {
        if (request == null || request.requestId() == null || blank(request.otpCode()) || blank(request.actionCode()) || blank(request.targetModule())) {
            throw new BadRequestException("Step-up request id, action code, target module and OTP are required");
        }
        StepUpVerificationChallenge challenge = stepUpChallengeRepository.findTopByRequestIdOrderByIdDesc(request.requestId())
                .orElseThrow(() -> new ResourceNotFoundException("Step-up challenge not found"));
        if (userId != null && (challenge.getUser() == null || !userId.equals(challenge.getUser().getId()))) {
            throw new ForbiddenException("You do not have permission to verify this step-up challenge");
        }
        if (!challenge.getActionCode().equalsIgnoreCase(request.actionCode().trim())
                || !challenge.getTargetModule().equalsIgnoreCase(request.targetModule().trim())
                || !sameTargetId(challenge.getTargetId(), request.targetId())) {
            throw new BadRequestException("Step-up challenge metadata did not match the requested action");
        }

        OtpVerificationRequest entity = verifyOtpEntity(
                challenge.getRequest().getId(),
                request.otpCode().trim(),
                "STEP_UP_VERIFY",
                blank(username) ? "SYSTEM" : username.trim(),
                ipAddress,
                deviceInfo,
                false
        );
        challenge.setRequest(entity);
        challenge.setVerificationToken(UUID.randomUUID().toString() + "-" + UUID.randomUUID());
        challenge.setVerifiedAt(LocalDateTime.now());
        challenge.setTokenExpiresAt(LocalDateTime.now().plusMinutes(STEP_UP_TOKEN_EXPIRY_MINUTES));
        challenge.setConsumedAt(null);
        stepUpChallengeRepository.save(challenge);

        return new StepUpVerifyResponse(
                challenge.getVerificationToken(),
                challenge.getActionCode(),
                challenge.getTargetModule(),
                challenge.getTargetId(),
                challenge.getVerifiedAt(),
                challenge.getTokenExpiresAt()
        );
    }

    public StepUpAuthorization validateStepUpToken(String token, Long userId, String actionCode, String targetModule, Long targetId) {
        if (blank(token)) {
            throw new ForbiddenException("Step-up verification is required for this action");
        }
        StepUpVerificationChallenge challenge = stepUpChallengeRepository.findTopByVerificationTokenAndStatusOrderByIdDesc(token.trim(), RecordStatus.ACTIVE)
                .orElseThrow(() -> new ForbiddenException("Invalid step-up verification token"));
        if (challenge.getUser() == null || userId == null || !userId.equals(challenge.getUser().getId())) {
            throw new ForbiddenException("This verification token does not belong to the current user");
        }
        if (!challenge.getActionCode().equalsIgnoreCase(actionCode)
                || !challenge.getTargetModule().equalsIgnoreCase(targetModule)
                || !sameTargetId(challenge.getTargetId(), targetId)) {
            throw new ForbiddenException("This verification token does not match the requested action");
        }
        if (challenge.getVerifiedAt() == null || challenge.getTokenExpiresAt() == null || challenge.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ForbiddenException("Step-up verification token has expired");
        }
        if (challenge.getConsumedAt() != null) {
            throw new ForbiddenException("Step-up verification token was already used");
        }
        return new StepUpAuthorization(challenge.getId(), challenge.getVerificationToken());
    }

    public void consumeStepUpToken(Long challengeId) {
        if (challengeId == null) {
            return;
        }
        StepUpVerificationChallenge challenge = stepUpChallengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Step-up challenge not found"));
        challenge.setConsumedAt(LocalDateTime.now());
        stepUpChallengeRepository.save(challenge);
    }

    public String getStepUpTokenHeaderName() {
        return STEP_UP_REQUEST_HEADER;
    }

    public OtpVerificationRequest resendLoginOtp(Long requestId) {
        OtpVerificationRequest entity = getRequest(requestId);
        if (entity.getPurpose() != VerificationPurpose.LOGIN_OTP) {
            throw new BadRequestException("Request is not a login OTP request");
        }
        return refreshOtp(entity, "Login OTP resent from auth flow");
    }

    public OtpVerificationRequest verifyLoginOtp(Long requestId, String otpCode, String ipAddress, String deviceInfo, String createdBy) {
        if (requestId == null || blank(otpCode)) {
            throw new BadRequestException("Login OTP request id and OTP code are required");
        }
        OtpVerificationRequest entity = getRequest(requestId);
        if (entity.getPurpose() != VerificationPurpose.LOGIN_OTP) {
            throw new BadRequestException("Request is not a login OTP request");
        }
        if (entity.getRequestStatus() == VerificationStatus.VERIFIED) {
            return entity;
        }
        validateBeforeVerification(entity);

        entity.setAttemptCount(entity.getAttemptCount() + 1);
        if (!OtpHashUtil.matches(otpCode, entity.getTokenCodeHash())) {
            if (entity.getAttemptCount() >= entity.getMaxAttemptCount()) {
                entity.setRequestStatus(VerificationStatus.FAILED);
            }
            requestRepository.save(entity);
            logAttempt(entity, "LOGIN_OTP_VERIFY", entity.getContactValue(), entity.getRequestStatus().name(),
                    "Invalid login OTP submitted", ipAddress, deviceInfo, normalize(createdBy));
            throw new BadRequestException("OTP did not match");
        }

        entity.setRequestStatus(VerificationStatus.VERIFIED);
        entity.setUsedAt(LocalDateTime.now());
        requestRepository.save(entity);
        logAttempt(entity, "LOGIN_OTP_VERIFY", entity.getContactValue(), VerificationStatus.VERIFIED.name(),
                "Login OTP verified successfully", ipAddress, deviceInfo, normalize(createdBy));
        return entity;
    }

    public String extractPreviewCode(String providerResponse) {
        if (blank(providerResponse)) {
            return null;
        }
        Matcher matcher = OTP_PREVIEW_PATTERN.matcher(providerResponse);
        return matcher.find() ? matcher.group(1) : null;
    }

    public VerificationLogResponse verifyOtp(VerificationOtpVerifyRequest request) {
        if (request == null || request.requestId() == null || blank(request.otpCode())) {
            throw new BadRequestException("Request id and OTP code are required");
        }
        OtpVerificationRequest entity = verifyOtpEntity(
                request.requestId(),
                request.otpCode(),
                "VERIFY_OTP",
                request.createdBy(),
                request.ipAddress(),
                request.deviceInfo(),
                true
        );

        passwordResetRequestRepository.findByRequestId(entity.getId()).ifPresent(reset -> {
            reset.setResetStatus(VerificationStatus.VERIFIED);
            reset.setResetTokenHash(entity.getTokenCodeHash());
            passwordResetRequestRepository.save(reset);
        });

        VerificationLogResponse response = mapLog(entity);
        publishVerificationEvent("OTP Verified", response.purpose(), response.referenceModule(), "SUCCESS");
        return response;
    }

    public VerificationLogResponse resendOtp(Long requestId) {
        OtpVerificationRequest entity = getRequest(requestId);
        if (entity.getRequestStatus() == VerificationStatus.VERIFIED) {
            throw new BadRequestException("Verified OTP cannot be resent");
        }
        if (entity.getSentAt() != null && entity.getSentAt().plusSeconds(RESEND_COOLDOWN_SECONDS).isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Resend cooldown is still active");
        }
        VerificationLogResponse response = mapLog(refreshOtp(entity, "OTP resent by verification console"));
        publishVerificationEvent("OTP Resent", response.purpose(), response.referenceModule(), "WARNING");
        return response;
    }

    private void publishVerificationEvent(String title, String purpose, String referenceModule, String severity) {
        String moduleLabel = blank(referenceModule) ? "VERIFICATION" : referenceModule.trim().toUpperCase(Locale.ROOT);
        liveUpdateGateway.publish(
                "VERIFICATION",
                title,
                purpose + " activity was completed for " + moduleLabel + ".",
                severity,
                "/verification/dashboard",
                null,
                null,
                "VERIFICATION_ACCESS"
        );
    }

    public VerificationLogResponse expireOtp(Long requestId) {
        OtpVerificationRequest entity = getRequest(requestId);
        entity.setRequestStatus(VerificationStatus.EXPIRED);
        entity.setExpiresAt(LocalDateTime.now());
        requestRepository.save(entity);
        logAttempt(entity, "EXPIRE", entity.getContactValue(), VerificationStatus.EXPIRED.name(), "OTP manually expired", null, null, "SYSTEM");
        return mapLog(entity);
    }

    public VerificationLogResponse markFailed(Long requestId) {
        OtpVerificationRequest entity = getRequest(requestId);
        entity.setRequestStatus(VerificationStatus.FAILED);
        requestRepository.save(entity);
        logAttempt(entity, "MARK_FAILED", entity.getContactValue(), VerificationStatus.FAILED.name(), "Marked failed from admin console", null, null, "SYSTEM");
        return mapLog(entity);
    }

    public VerificationLogResponse forgotPassword(ForgotPasswordRequest request) {
        if (request == null || blank(request.identifier()) || blank(request.channelType())) {
            throw new BadRequestException("Identifier and channel type are required");
        }
        ChannelType channelType = parseChannelType(request.channelType());
        User user = resolveUserForPasswordReset(request.identifier(), channelType);
        String contactValue = channelType == ChannelType.EMAIL ? user.getEmail() : user.getMobile();
        OtpVerificationRequest otpRequest = sendOtp(contactValue, channelType, VerificationPurpose.PASSWORD_RESET, user.getId(), null, "USER", user.getId(), "Password reset token issued", TokenType.PASSWORD_RESET);

        PasswordResetRequest reset = passwordResetRequestRepository.findByRequestId(otpRequest.getId()).orElseGet(PasswordResetRequest::new);
        reset.setRequest(otpRequest);
        reset.setIdentifier(request.identifier().trim());
        reset.setChannelType(channelType);
        reset.setExpiresAt(otpRequest.getExpiresAt());
        reset.setResetStatus(VerificationStatus.SENT);
        passwordResetRequestRepository.save(reset);
        VerificationLogResponse response = mapLog(otpRequest);
        publishVerificationEvent("Password Reset OTP Sent", response.purpose(), response.referenceModule(), "INFO");
        return response;
    }

    public VerificationLogResponse resetPassword(ResetPasswordRequest request) {
        if (request == null || request.requestId() == null || blank(request.otpCode()) || blank(request.newPassword()) || blank(request.confirmPassword())) {
            throw new BadRequestException("Reset password input is incomplete");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BadRequestException("Password confirmation did not match");
        }
        if (request.newPassword().trim().length() < 4) {
            throw new BadRequestException("New password must be at least 4 characters");
        }

        OtpVerificationRequest otpRequest = getRequest(request.requestId());
        PasswordResetRequest resetRequest = passwordResetRequestRepository.findByRequestId(otpRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Password reset request not found"));

        validateBeforeVerification(otpRequest);
        otpRequest.setAttemptCount(otpRequest.getAttemptCount() + 1);
        if (!OtpHashUtil.matches(request.otpCode(), otpRequest.getTokenCodeHash())) {
            if (otpRequest.getAttemptCount() >= otpRequest.getMaxAttemptCount()) {
                otpRequest.setRequestStatus(VerificationStatus.FAILED);
                resetRequest.setResetStatus(VerificationStatus.FAILED);
            }
            requestRepository.save(otpRequest);
            passwordResetRequestRepository.save(resetRequest);
            logAttempt(otpRequest, "RESET_PASSWORD", otpRequest.getContactValue(), otpRequest.getRequestStatus().name(), "Password reset OTP mismatch", null, null, "SYSTEM");
            throw new BadRequestException("OTP did not match");
        }

        User user = otpRequest.getUser();
        if (user == null) {
            throw new BadRequestException("Password reset request was not linked to a user");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword().trim()));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        otpRequest.setRequestStatus(VerificationStatus.VERIFIED);
        otpRequest.setUsedAt(LocalDateTime.now());
        resetRequest.setResetStatus(VerificationStatus.VERIFIED);
        resetRequest.setUsedAt(LocalDateTime.now());
        resetRequest.setResetTokenHash(otpRequest.getTokenCodeHash());
        requestRepository.save(otpRequest);
        passwordResetRequestRepository.save(resetRequest);
        logAttempt(otpRequest, "RESET_PASSWORD", otpRequest.getContactValue(), VerificationStatus.VERIFIED.name(), "Password reset completed", null, null, user.getUsername());
        if (!blank(user.getEmail())) {
            automatedMailService.sendPasswordResetSuccessEmail(user.getEmail(), user.getFullName());
        }
        VerificationLogResponse response = mapLog(otpRequest);
        publishVerificationEvent("Password Reset Completed", response.purpose(), response.referenceModule(), "SUCCESS");
        return response;
    }

    private OtpVerificationRequest verifyOtpEntity(Long requestId, String otpCode, String attemptType, String createdBy,
                                                   String ipAddress, String deviceInfo, boolean updateReferenceFlags) {
        OtpVerificationRequest entity = getRequest(requestId);
        if (entity.getRequestStatus() == VerificationStatus.VERIFIED) {
            return entity;
        }
        validateBeforeVerification(entity);

        entity.setAttemptCount(entity.getAttemptCount() + 1);
        if (!OtpHashUtil.matches(otpCode, entity.getTokenCodeHash())) {
            if (entity.getAttemptCount() >= entity.getMaxAttemptCount()) {
                entity.setRequestStatus(VerificationStatus.FAILED);
            }
            requestRepository.save(entity);
            logAttempt(entity, attemptType, entity.getContactValue(), entity.getRequestStatus().name(), "Invalid OTP submitted", ipAddress, deviceInfo, createdBy);
            throw new BadRequestException("OTP did not match");
        }

        entity.setRequestStatus(VerificationStatus.VERIFIED);
        entity.setUsedAt(LocalDateTime.now());
        requestRepository.save(entity);
        logAttempt(entity, attemptType, entity.getContactValue(), VerificationStatus.VERIFIED.name(), "OTP verified successfully", ipAddress, deviceInfo, createdBy);
        if (updateReferenceFlags) {
            updateVerifiedFlags(entity, normalize(createdBy));
        }
        return entity;
    }

    private OtpVerificationRequest sendOtp(String contactValue, ChannelType channelType, VerificationPurpose purpose, Long userId,
                                           Long customerId, String referenceModule, Long referenceId, String remarks, TokenType tokenType) {
        validateContact(contactValue, channelType);
        ensureNoActiveOtp(contactValue, purpose);

        User user = userId == null ? null : userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Customer customer = customerId == null ? null : customerRepository.findActiveById(customerId).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        validateReferenceState(user, customer);

        String normalizedContact = contactValue.trim();
        String otpCode = OtpGeneratorUtil.generateSixDigitCode();
        VerificationTemplate template = resolveTemplate(purpose, channelType);
        boolean asyncDelivery = shouldDispatchAsync(channelType, purpose);
        OtpDeliveryResult deliveryResult = null;
        if (!asyncDelivery) {
            deliveryResult = deliveryService.deliver(channelType, normalizedContact, purpose, otpCode, remarks, template);
            ensureDeliverySuccess(deliveryResult, channelType);
        }

        OtpVerificationRequest entity = new OtpVerificationRequest();
        entity.setUser(user);
        entity.setCustomer(customer);
        entity.setReferenceModule(normalize(referenceModule));
        entity.setReferenceId(referenceId);
        entity.setPurpose(purpose);
        entity.setChannelType(channelType);
        entity.setContactValue(normalizedContact);
        entity.setTokenCodeHash(OtpHashUtil.hash(otpCode));
        entity.setTokenType(tokenType);
        entity.setSentAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        entity.setAttemptCount(0);
        entity.setMaxAttemptCount(DEFAULT_MAX_ATTEMPTS);
        entity.setRequestStatus(asyncDelivery ? VerificationStatus.PENDING : VerificationStatus.SENT);
        entity.setProviderResponse(asyncDelivery ? "provider=GMAIL_SMTP; status=QUEUED_ASYNC" : deliveryResult.providerResponse());
        requestRepository.save(entity);

        VerificationDispatchQueue queue = createDispatchQueue(
                entity,
                channelType,
                asyncDelivery ? "GMAIL_SMTP" : deliveryResult.providerName(),
                asyncDelivery ? VerificationStatus.PENDING : VerificationStatus.SENT,
                entity.getProviderResponse(),
                asyncDelivery ? null : LocalDateTime.now()
        );

        if (asyncDelivery) {
            logAttempt(entity, "SEND_OTP", normalizedContact, VerificationStatus.PENDING.name(), "OTP email queued for background SMTP delivery", null, null, "SYSTEM");
            dispatchOtpAsync(entity.getId(), queue.getId(), channelType, normalizedContact, purpose, otpCode, remarks, template,
                    "SEND_OTP", "OTP dispatched through configured provider");
        } else {
            logAttempt(entity, "SEND_OTP", normalizedContact, VerificationStatus.SENT.name(), "OTP dispatched through configured provider", null, null, "SYSTEM");
        }
        syncPendingContactStatus(entity);

        passwordResetRequestRepository.findByRequestId(entity.getId()).ifPresent(existing -> {
            existing.setExpiresAt(entity.getExpiresAt());
            existing.setResetStatus(VerificationStatus.SENT);
            passwordResetRequestRepository.save(existing);
        });
        return entity;
    }

    private OtpVerificationRequest refreshOtp(OtpVerificationRequest entity, String remark) {
        String otpCode = OtpGeneratorUtil.generateSixDigitCode();
        VerificationTemplate template = resolveTemplate(entity.getPurpose(), entity.getChannelType());
        boolean asyncDelivery = shouldDispatchAsync(entity.getChannelType(), entity.getPurpose());
        OtpDeliveryResult deliveryResult = null;
        if (!asyncDelivery) {
            deliveryResult = deliveryService.deliver(entity.getChannelType(), entity.getContactValue(), entity.getPurpose(), otpCode, remark, template);
            ensureDeliverySuccess(deliveryResult, entity.getChannelType());
        }

        entity.setTokenCodeHash(OtpHashUtil.hash(otpCode));
        entity.setSentAt(LocalDateTime.now());
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        entity.setAttemptCount(0);
        entity.setRequestStatus(asyncDelivery ? VerificationStatus.PENDING : VerificationStatus.SENT);
        entity.setProviderResponse(asyncDelivery ? "provider=GMAIL_SMTP; status=QUEUED_ASYNC" : deliveryResult.providerResponse());
        requestRepository.save(entity);

        VerificationDispatchQueue queue = createDispatchQueue(
                entity,
                entity.getChannelType(),
                asyncDelivery ? "GMAIL_SMTP" : deliveryResult.providerName(),
                asyncDelivery ? VerificationStatus.PENDING : VerificationStatus.SENT,
                entity.getProviderResponse(),
                asyncDelivery ? null : LocalDateTime.now()
        );

        if (asyncDelivery) {
            logAttempt(entity, "RESEND_OTP", entity.getContactValue(), VerificationStatus.PENDING.name(), "OTP email queued for background SMTP delivery", null, null, "SYSTEM");
            dispatchOtpAsync(entity.getId(), queue.getId(), entity.getChannelType(), entity.getContactValue(), entity.getPurpose(), otpCode, remark, template,
                    "RESEND_OTP", remark);
        } else {
            logAttempt(entity, "RESEND_OTP", entity.getContactValue(), VerificationStatus.SENT.name(), remark, null, null, "SYSTEM");
        }
        return entity;
    }

    private boolean shouldDispatchAsync(ChannelType channelType, VerificationPurpose purpose) {
        return channelType == ChannelType.EMAIL && purpose == VerificationPurpose.LOGIN_OTP;
    }

    private VerificationDispatchQueue createDispatchQueue(OtpVerificationRequest entity, ChannelType channelType,
                                                         String providerName, VerificationStatus dispatchStatus,
                                                         String providerResponse, LocalDateTime dispatchedAt) {
        VerificationDispatchQueue queue = new VerificationDispatchQueue();
        queue.setRequest(entity);
        queue.setChannelType(channelType);
        queue.setProviderName(resolveProviderName(channelType, providerName));
        queue.setDispatchStatus(dispatchStatus);
        queue.setProviderResponse(providerResponse);
        queue.setDispatchedAt(dispatchedAt);
        return queueRepository.save(queue);
    }

    private void dispatchOtpAsync(Long requestId, Long queueId, ChannelType channelType, String contactValue,
                                  VerificationPurpose purpose, String otpCode, String remarks,
                                  VerificationTemplate template, String attemptType, String successRemark) {
        deliveryService.deliverAsync(channelType, contactValue, purpose, otpCode, remarks, template)
                .whenComplete((deliveryResult, error) -> {
                    OtpVerificationRequest savedRequest = requestRepository.findById(requestId).orElse(null);
                    VerificationDispatchQueue savedQueue = queueRepository.findById(queueId).orElse(null);
                    if (error != null || deliveryResult == null || !deliveryResult.delivered()) {
                        String providerResponse = error != null
                                ? "provider=GMAIL_SMTP; status=FAILED; error=" + error.getMessage()
                                : deliveryResult == null ? "provider=GMAIL_SMTP; status=FAILED; error=No provider response" : deliveryResult.providerResponse();
                        if (savedRequest != null) {
                            if (savedRequest.getRequestStatus() != VerificationStatus.VERIFIED) {
                                savedRequest.setRequestStatus(VerificationStatus.FAILED);
                            }
                            savedRequest.setProviderResponse(providerResponse);
                            requestRepository.save(savedRequest);
                            logAttempt(savedRequest, attemptType, savedRequest.getContactValue(), VerificationStatus.FAILED.name(),
                                    "OTP email delivery failed in background SMTP dispatch", null, null, "SYSTEM");
                        }
                        if (savedQueue != null) {
                            savedQueue.setDispatchStatus(VerificationStatus.FAILED);
                            savedQueue.setProviderResponse(providerResponse);
                            savedQueue.setDispatchedAt(LocalDateTime.now());
                            queueRepository.save(savedQueue);
                        }
                        return;
                    }

                    if (savedRequest != null) {
                        if (savedRequest.getRequestStatus() != VerificationStatus.VERIFIED) {
                            savedRequest.setRequestStatus(VerificationStatus.SENT);
                        }
                        savedRequest.setProviderResponse(deliveryResult.providerResponse());
                        requestRepository.save(savedRequest);
                        logAttempt(savedRequest, attemptType, savedRequest.getContactValue(), VerificationStatus.SENT.name(), successRemark, null, null, "SYSTEM");
                    }
                    if (savedQueue != null) {
                        savedQueue.setProviderName(resolveProviderName(channelType, deliveryResult.providerName()));
                        savedQueue.setDispatchStatus(VerificationStatus.SENT);
                        savedQueue.setProviderResponse(deliveryResult.providerResponse());
                        savedQueue.setDispatchedAt(LocalDateTime.now());
                        queueRepository.save(savedQueue);
                    }
                });
    }
    private void updateVerifiedFlags(OtpVerificationRequest entity, String verifiedBy) {
        if (entity.getPurpose() == VerificationPurpose.VERIFY_EMAIL) {
            if (entity.getUser() != null) {
                entity.getUser().setEmailVerified(true);
                userRepository.save(entity.getUser());
            }
            if (entity.getCustomer() != null) {
                entity.getCustomer().setEmailVerified(true);
                customerRepository.update(entity.getCustomer());
            }
        }
        if (entity.getPurpose() == VerificationPurpose.VERIFY_MOBILE) {
            if (entity.getUser() != null) {
                entity.getUser().setMobileVerified(true);
                userRepository.save(entity.getUser());
            }
            if (entity.getCustomer() != null) {
                entity.getCustomer().setMobileVerified(true);
                customerRepository.update(entity.getCustomer());
            }
        }
        syncVerifiedContactStatus(entity, verifiedBy);
    }

    private void validateReferenceState(User user, Customer customer) {
        if (user != null && user.getStatus() == UserStatus.LOCKED) {
            throw new BadRequestException("User is locked and cannot receive OTP");
        }
        if (customer != null && customer.getCustomerStatus() == CustomerStatus.BLOCKED) {
            throw new BadRequestException("Blocked customer cannot receive OTP");
        }
    }

    private void validateBeforeVerification(OtpVerificationRequest entity) {
        if (entity.getRequestStatus() == VerificationStatus.EXPIRED || entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            entity.setRequestStatus(VerificationStatus.EXPIRED);
            requestRepository.save(entity);
            throw new BadRequestException("OTP has expired");
        }
        if (entity.getRequestStatus() == VerificationStatus.FAILED) {
            throw new BadRequestException("OTP request has already failed");
        }
    }

    private void ensureNoActiveOtp(String contactValue, VerificationPurpose purpose) {
        List<OtpVerificationRequest> active = requestRepository.findActiveForContact(contactValue, purpose,
                List.of(VerificationStatus.PENDING, VerificationStatus.SENT), LocalDateTime.now());
        if (!active.isEmpty()) {
            throw new BadRequestException("An active unexpired OTP already exists for this contact and purpose");
        }
    }

    private String resolveContact(VerificationSendOtpRequest request, ChannelType channelType) {
        if (request == null) {
            throw new BadRequestException("Verification request is required");
        }
        if (!blank(request.contactValue())) {
            return request.contactValue().trim();
        }
        if (request.userId() != null) {
            User user = userRepository.findById(request.userId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return channelType == ChannelType.EMAIL ? user.getEmail() : user.getMobile();
        }
        if (request.customerId() != null) {
            Customer customer = customerRepository.findActiveById(request.customerId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            return channelType == ChannelType.EMAIL ? customer.getEmail() : customer.getMobile();
        }
        throw new BadRequestException("Contact value is required");
    }

    private User resolveUserForPasswordReset(String identifier, ChannelType channelType) {
        String trimmed = identifier.trim();
        Optional<User> user = userRepository.findByUsername(trimmed);
        if (user.isEmpty()) {
            user = channelType == ChannelType.EMAIL ? userRepository.findByEmail(trimmed) : userRepository.findByMobile(trimmed);
        }
        User value = user.orElseThrow(() -> new ResourceNotFoundException("User not found for password reset"));
        if (value.getStatus() == UserStatus.LOCKED) {
            throw new BadRequestException("Locked user cannot reset password");
        }
        return value;
    }

    private OtpVerificationRequest getRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification request not found"));
    }

    private void validateContact(String contactValue, ChannelType channelType) {
        if (blank(contactValue)) {
            throw new BadRequestException("Contact value is required");
        }
        String trimmed = contactValue.trim();
        if (channelType == ChannelType.EMAIL && !trimmed.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BadRequestException("Valid email format is required");
        }
        if (channelType == ChannelType.SMS && !trimmed.matches("^(?:\\+?88)?01[3-9]\\d{8}$")) {
            throw new BadRequestException("Valid mobile format is required");
        }
    }

    private VerificationTemplate resolveTemplate(VerificationPurpose purpose, ChannelType channelType) {
        return templateRepository.findFirstByPurposeAndChannelTypeAndStatus(purpose, channelType, RecordStatus.ACTIVE)
                .orElse(null);
    }

    private void ensureDeliverySuccess(OtpDeliveryResult deliveryResult, ChannelType channelType) {
        if (deliveryResult != null && deliveryResult.delivered()) {
            return;
        }
        String contactLabel = channelType == ChannelType.EMAIL ? "email" : "mobile";
        throw new BadRequestException("Unable to dispatch OTP to the configured " + contactLabel + " channel right now. Please contact support.");
    }

    private String resolveProviderName(ChannelType channelType, String preferredProviderName) {
        if (!blank(preferredProviderName)) {
            return preferredProviderName.trim();
        }
        String code = channelType == ChannelType.EMAIL ? "EMAIL" : "SMS";
        return channelRepository.findByChannelCode(code)
                .map(VerificationChannel::getProviderName)
                .orElse("LOCAL_SIMULATOR");
    }

    private void logAttempt(OtpVerificationRequest request, String type, String rawValue, String status, String remarks,
                            String ipAddress, String deviceInfo, String createdBy) {
        VerificationAttemptLog log = new VerificationAttemptLog();
        log.setRequest(request);
        log.setAttemptType(type);
        log.setAttemptValueMasked(MaskingUtil.mask(rawValue, request.getChannelType() == ChannelType.EMAIL));
        log.setAttemptStatus(status);
        log.setRemarks(remarks);
        log.setIpAddress(normalize(ipAddress));
        log.setDeviceInfo(normalize(deviceInfo));
        log.setCreatedBy(normalize(createdBy));
        attemptLogRepository.save(log);
    }

    private VerificationLogResponse mapLog(OtpVerificationRequest entity) {
        if (entity == null) {
            throw new ResourceNotFoundException("Verification request not found");
        }
        List<VerificationAttemptResponse> attempts = attemptLogRepository.findByRequestIdOrderByIdAsc(entity.getId()).stream()
                .map(this::mapAttempt)
                .toList();
        String lastDispatchStatus = queueRepository.findByRequestIdOrderByIdDesc(entity.getId()).stream()
                .findFirst()
                .map(item -> item.getDispatchStatus().name())
                .orElse(entity.getRequestStatus().name());
        return new VerificationLogResponse(
                entity.getId(),
                entity.getPurpose().name(),
                entity.getChannelType().name(),
                entity.getTokenType().name(),
                entity.getContactValue(),
                MaskingUtil.mask(entity.getContactValue(), entity.getChannelType() == ChannelType.EMAIL),
                entity.getUser() == null ? null : entity.getUser().getId(),
                entity.getUser() == null ? null : entity.getUser().getUsername(),
                entity.getCustomer() == null ? null : entity.getCustomer().getId(),
                entity.getCustomer() == null ? null : entity.getCustomer().getCustomerCode(),
                entity.getCustomer() == null ? null : entity.getCustomer().getFullName(),
                entity.getReferenceModule(),
                entity.getReferenceId(),
                entity.getAttemptCount(),
                entity.getMaxAttemptCount(),
                entity.getRequestStatus().name(),
                entity.getProviderResponse(),
                entity.getSentAt(),
                entity.getExpiresAt(),
                entity.getUsedAt(),
                isVerified(entity),
                lastDispatchStatus,
                attempts
        );
    }

    private VerificationAttemptResponse mapAttempt(VerificationAttemptLog item) {
        return new VerificationAttemptResponse(
                item.getId(),
                item.getAttemptType(),
                item.getAttemptValueMasked(),
                item.getAttemptStatus(),
                item.getRemarks(),
                item.getIpAddress(),
                item.getDeviceInfo(),
                item.getCreatedBy(),
                item.getCreatedAt()
        );
    }

    private VerificationContactStatusResponse mapContactStatus(ContactVerificationStatus item) {
        return new VerificationContactStatusResponse(
                item.getId(),
                item.getReferenceModule(),
                item.getReferenceId(),
                item.getContactType(),
                MaskingUtil.mask(item.getContactValue(), "EMAIL".equalsIgnoreCase(item.getContactType())),
                item.getIsPrimary(),
                item.getIsVerified(),
                item.getVerifiedAt(),
                item.getVerifiedBy(),
                item.getVerificationMethod(),
                item.getLastVerificationRequest() == null ? null : item.getLastVerificationRequest().getId(),
                item.getStatus().name(),
                item.getUpdatedAt()
        );
    }

    private StepUpChallengeResponse mapStepUpChallenge(StepUpVerificationChallenge challenge, OtpVerificationRequest request) {
        return new StepUpChallengeResponse(
                request.getId(),
                challenge.getActionCode(),
                challenge.getTargetModule(),
                challenge.getTargetId(),
                request.getChannelType().name(),
                request.getChannelType() == ChannelType.EMAIL
                        ? MaskingUtil.mask(request.getContactValue(), true)
                        : MaskingUtil.mask(request.getContactValue(), false),
                request.getExpiresAt()
        );
    }

    private boolean matchesKeyword(OtpVerificationRequest item, String keyword) {
        if (blank(keyword)) {
            return true;
        }
        String value = keyword.trim().toLowerCase(Locale.ROOT);
        return contains(item.getContactValue(), value)
                || contains(item.getPurpose().name(), value)
                || contains(item.getChannelType().name(), value)
                || contains(item.getReferenceModule(), value)
                || (item.getUser() != null && contains(item.getUser().getUsername(), value))
                || (item.getCustomer() != null && (contains(item.getCustomer().getCustomerCode(), value) || contains(item.getCustomer().getFullName(), value)));
    }

    private boolean isVerified(OtpVerificationRequest entity) {
        if (entity.getPurpose() == VerificationPurpose.VERIFY_EMAIL) {
            if (entity.getUser() != null) {
                return Boolean.TRUE.equals(entity.getUser().getEmailVerified());
            }
            if (entity.getCustomer() != null) {
                return Boolean.TRUE.equals(entity.getCustomer().getEmailVerified());
            }
        }
        if (entity.getPurpose() == VerificationPurpose.VERIFY_MOBILE) {
            if (entity.getUser() != null) {
                return Boolean.TRUE.equals(entity.getUser().getMobileVerified());
            }
            if (entity.getCustomer() != null) {
                return Boolean.TRUE.equals(entity.getCustomer().getMobileVerified());
            }
        }
        return entity.getRequestStatus() == VerificationStatus.VERIFIED;
    }

    private ChannelType parseChannelType(String value) {
        try {
            return ChannelType.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new BadRequestException("Valid channel type is required");
        }
    }

    private VerificationPurpose parsePurpose(String value, VerificationPurpose fallback) {
        if (blank(value)) {
            return fallback;
        }
        try {
            return VerificationPurpose.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new BadRequestException("Valid verification purpose is required");
        }
    }

    private boolean contains(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private boolean sameTargetId(Long left, Long right) {
        return left == null ? right == null : left.equals(right);
    }

    private void syncPendingContactStatus(OtpVerificationRequest entity) {
        if (!isContactVerificationPurpose(entity.getPurpose())) {
            return;
        }
        ContactVerificationStatus status = getOrCreateContactStatus(entity);
        status.setIsVerified(false);
        status.setVerifiedAt(null);
        status.setVerifiedBy(null);
        status.setVerificationMethod(entity.getChannelType().name() + "_OTP");
        status.setLastVerificationRequest(entity);
        contactVerificationStatusRepository.save(status);
    }

    private void syncVerifiedContactStatus(OtpVerificationRequest entity, String verifiedBy) {
        if (!isContactVerificationPurpose(entity.getPurpose())) {
            return;
        }
        ContactVerificationStatus status = getOrCreateContactStatus(entity);
        status.setIsVerified(true);
        status.setVerifiedAt(LocalDateTime.now());
        status.setVerifiedBy(blank(verifiedBy) ? "SYSTEM" : verifiedBy.trim());
        status.setVerificationMethod(entity.getChannelType().name() + "_OTP");
        status.setLastVerificationRequest(entity);
        contactVerificationStatusRepository.save(status);
    }

    private ContactVerificationStatus getOrCreateContactStatus(OtpVerificationRequest entity) {
        String referenceModule = deriveReferenceModule(entity);
        Long referenceId = deriveReferenceId(entity);
        String contactType = entity.getPurpose() == VerificationPurpose.VERIFY_EMAIL ? "EMAIL" : "MOBILE";
        return contactVerificationStatusRepository
                .findTopByReferenceModuleAndReferenceIdAndContactTypeAndContactValueAndStatusOrderByIdDesc(
                        referenceModule, referenceId, contactType, entity.getContactValue(), RecordStatus.ACTIVE
                )
                .orElseGet(() -> {
                    ContactVerificationStatus item = new ContactVerificationStatus();
                    item.setReferenceModule(referenceModule);
                    item.setReferenceId(referenceId);
                    item.setContactType(contactType);
                    item.setContactValue(entity.getContactValue());
                    item.setIsPrimary(true);
                    item.setStatus(RecordStatus.ACTIVE);
                    return item;
                });
    }

    private String deriveReferenceModule(OtpVerificationRequest entity) {
        if (!blank(entity.getReferenceModule())) {
            return entity.getReferenceModule().trim().toUpperCase(Locale.ROOT);
        }
        if (entity.getCustomer() != null) {
            return "CUSTOMER";
        }
        if (entity.getUser() != null) {
            return "USER";
        }
        return "GENERIC";
    }

    private Long deriveReferenceId(OtpVerificationRequest entity) {
        if (entity.getReferenceId() != null) {
            return entity.getReferenceId();
        }
        if (entity.getCustomer() != null) {
            return entity.getCustomer().getId();
        }
        if (entity.getUser() != null) {
            return entity.getUser().getId();
        }
        return 0L;
    }

    private boolean isContactVerificationPurpose(VerificationPurpose purpose) {
        return purpose == VerificationPurpose.VERIFY_EMAIL || purpose == VerificationPurpose.VERIFY_MOBILE;
    }

    private String normalize(String value) {
        return blank(value) ? null : value.trim();
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private ContactSelection resolvePreferredLoginContact(User user, String preferredChannel) {
        String normalized = blank(preferredChannel) ? "AUTO" : preferredChannel.trim().toUpperCase(Locale.ROOT);
        if ("EMAIL".equals(normalized)) {
            return resolveSpecificLoginContact(user, ChannelType.EMAIL);
        }
        if ("SMS".equals(normalized) || "MOBILE".equals(normalized)) {
            return resolveSpecificLoginContact(user, ChannelType.SMS);
        }
        if (user.getEmailVerified() != null && user.getEmailVerified() && !blank(user.getEmail())) {
            validateContact(user.getEmail(), ChannelType.EMAIL);
            return new ContactSelection(ChannelType.EMAIL, user.getEmail().trim());
        }
        if (user.getMobileVerified() != null && user.getMobileVerified() && !blank(user.getMobile())) {
            validateContact(user.getMobile(), ChannelType.SMS);
            return new ContactSelection(ChannelType.SMS, user.getMobile().trim());
        }
        if (!blank(user.getEmail())) {
            validateContact(user.getEmail(), ChannelType.EMAIL);
            return new ContactSelection(ChannelType.EMAIL, user.getEmail().trim());
        }
        if (!blank(user.getMobile())) {
            validateContact(user.getMobile(), ChannelType.SMS);
            return new ContactSelection(ChannelType.SMS, user.getMobile().trim());
        }
        throw new BadRequestException("No valid email or mobile found for login OTP");
    }

    private ContactSelection resolveSpecificLoginContact(User user, ChannelType channelType) {
        if (channelType == ChannelType.EMAIL && !blank(user.getEmail())) {
            validateContact(user.getEmail(), ChannelType.EMAIL);
            return new ContactSelection(ChannelType.EMAIL, user.getEmail().trim());
        }
        if (channelType == ChannelType.SMS && !blank(user.getMobile())) {
            validateContact(user.getMobile(), ChannelType.SMS);
            return new ContactSelection(ChannelType.SMS, user.getMobile().trim());
        }
        throw new BadRequestException(channelType == ChannelType.EMAIL
                ? "No email address is available for login OTP"
                : "No mobile number is available for login OTP");
    }

    private record ContactSelection(ChannelType channelType, String contactValue) {
    }

    public record StepUpAuthorization(Long challengeId, String verificationToken) {
    }
}

