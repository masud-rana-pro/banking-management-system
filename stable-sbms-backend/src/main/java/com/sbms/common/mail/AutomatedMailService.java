package com.sbms.common.mail;

import com.sbms.common.document.DocumentBrandingService;
import com.sbms.common.document.DocumentTemplateService;
import com.sbms.config.AutomatedMailProperties;
import com.sbms.user.repository.UserRepository;
import com.sbms.verification.config.VerificationDeliveryProperties;
import com.sbms.verification.enums.VerificationPurpose;
import org.springframework.core.io.FileSystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class AutomatedMailService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a", Locale.ENGLISH);
    private static final Logger log = LoggerFactory.getLogger(AutomatedMailService.class);
    private static final String LOGO_CONTENT_ID = "sbmsBankLogo";

    private final JavaMailSender mailSender;
    private final DocumentTemplateService documentTemplateService;
    private final DocumentBrandingService brandingService;
    private final AutomatedMailProperties properties;
    private final VerificationDeliveryProperties verificationDeliveryProperties;
    private final UserRepository userRepository;

    public AutomatedMailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                                DocumentTemplateService documentTemplateService,
                                DocumentBrandingService brandingService,
                                AutomatedMailProperties properties,
                                VerificationDeliveryProperties verificationDeliveryProperties,
                                UserRepository userRepository) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.documentTemplateService = documentTemplateService;
        this.brandingService = brandingService;
        this.properties = properties;
        this.verificationDeliveryProperties = verificationDeliveryProperties;
        this.userRepository = userRepository;
    }

    public boolean isConfigured() {
        return mailSender != null && !blank(defaultFromAddress());
    }

    public boolean sendOtpEmail(String recipient,
                                String subject,
                                VerificationPurpose purpose,
                                String otpCode,
                                int expiryMinutes,
                                String remarks) throws Exception {
        return sendHtmlMailOrThrow(recipient, subject, "mail/otp-email", defaultVariables(Map.of(
                "headline", otpHeadline(purpose),
                "intro", "Use the verification code below to continue your request securely.",
                "otpCode", otpCode,
                "expiryMinutes", expiryMinutes,
                "remarks", blank(remarks) ? null : remarks.trim(),
                "purposeLabel", prettyPurpose(purpose),
                "actionLabel", "Use OTP"
        )));
    }

    public boolean sendPasswordResetSuccessEmail(String recipient, String recipientName) {
        return sendHtmlMail(recipient, "Password reset completed", "mail/simple-notice", defaultVariables(Map.of(
                "headline", "Password reset completed",
                "intro", "Your SBMS password was changed successfully.",
                "primaryDetails", "If you did not perform this action, contact support immediately.",
                "secondaryDetails", "Completed at " + DATE_TIME_FORMATTER.format(LocalDateTime.now()),
                "ctaUrl", properties.getAppBaseUrl() + "/auth/login",
                "ctaLabel", "Sign In"
        )));
    }

    public boolean sendUserWelcomeEmail(String recipient, String fullName, String username, String roleName) {
        return sendHtmlMail(recipient, "Your SBMS account is ready", "mail/simple-notice", defaultVariables(Map.of(
                "headline", "Welcome to SBMS",
                "intro", "A new system account has been created for you.",
                "primaryDetails", "Username: " + username + "<br>Role: " + safe(roleName),
                "secondaryDetails", "For security, you must change your password after first sign-in.",
                "ctaUrl", properties.getAppBaseUrl() + "/auth/login",
                "ctaLabel", "Open Login"
        )));
    }

    public boolean sendAdminPasswordResetEmail(String recipient, String fullName, String username) {
        return sendHtmlMail(recipient, "Your SBMS password was reset", "mail/simple-notice", defaultVariables(Map.of(
                "headline", "Password reset by administrator",
                "intro", "Your system password has been reset by an authorized administrator.",
                "primaryDetails", "Username: " + username,
                "secondaryDetails", "You must sign in and set a new password before continuing normal use.",
                "ctaUrl", properties.getAppBaseUrl() + "/auth/login",
                "ctaLabel", "Sign In"
        )));
    }

    public boolean sendReportReadyEmail(String recipient, String reportName, String exportType) {
        return sendHtmlMail(recipient, reportName + " is ready", "mail/simple-notice", defaultVariables(Map.of(
                "headline", "Report ready",
                "intro", "Your requested report has been generated successfully.",
                "primaryDetails", "Report: " + reportName + "<br>Format: " + safe(exportType),
                "secondaryDetails", "You can preview or download it from the export history page.",
                "ctaUrl", properties.getAppBaseUrl() + "/reports/export-history",
                "ctaLabel", "Open Export History"
        )));
    }

    public boolean sendTransactionConfirmationEmail(String recipient,
                                                    String customerName,
                                                    String transactionType,
                                                    String transactionRef,
                                                    String amount,
                                                    String accountNumber,
                                                    String transactionDate,
                                                    String narration) {
        return sendHtmlMail(recipient, safe(transactionType) + " confirmation", "mail/simple-notice", defaultVariables(Map.of(
                "headline", safe(transactionType) + " completed",
                "intro", "Your transaction has been posted successfully in SBMS.",
                "primaryDetails", "Reference: " + safe(transactionRef)
                        + "<br>Type: " + safe(transactionType)
                        + "<br>Amount: " + safe(amount)
                        + "<br>Account: " + safe(accountNumber),
                "secondaryDetails", "Date: " + safe(transactionDate)
                        + (blank(narration) ? "" : "<br>Narration: " + safe(narration)),
                "ctaUrl", properties.getAppBaseUrl() + "/transactions/list",
                "ctaLabel", "Open Transactions"
        )));
    }

    public boolean sendApprovalDecisionEmail(String recipient,
                                             String moduleName,
                                             String referenceNo,
                                             String decision,
                                             String remarks,
                                             String routePath,
                                             String ctaLabel) {
        return sendHtmlMail(recipient, moduleName + " " + safe(decision), "mail/simple-notice", defaultVariables(Map.of(
                "headline", moduleName + " " + safe(decision),
                "intro", "An approval decision has been recorded in SBMS.",
                "primaryDetails", "Reference: " + safe(referenceNo) + "<br>Decision: " + safe(decision),
                "secondaryDetails", blank(remarks)
                        ? "Please open the module for full context."
                        : "Remarks: " + safe(remarks),
                "ctaUrl", blank(routePath) ? null : properties.getAppBaseUrl() + routePath,
                "ctaLabel", blank(ctaLabel) ? "Open Record" : ctaLabel
        )));
    }

    @Async("verificationDeliveryExecutor")
    public void sendApprovalDecisionEmailAsync(String recipient,
                                               String moduleName,
                                               String referenceNo,
                                               String decision,
                                               String remarks,
                                               String routePath,
                                               String ctaLabel) {
        sendApprovalDecisionEmail(recipient, moduleName, referenceNo, decision, remarks, routePath, ctaLabel);
    }

    public boolean sendOperationalAlertToSupport(String headline, String intro, String primaryDetails, String routePath, String ctaLabel) {
        boolean delivered = false;
        for (String recipient : resolveOperationalAlertRecipients()) {
            delivered = sendHtmlMail(recipient, headline, "mail/simple-notice", defaultVariables(Map.of(
                    "headline", headline,
                    "intro", intro,
                    "primaryDetails", primaryDetails,
                    "secondaryDetails", "This is an automated operations alert from SBMS.",
                    "ctaUrl", blank(routePath) ? null : properties.getAppBaseUrl() + routePath,
                    "ctaLabel", blank(ctaLabel) ? "Open Module" : ctaLabel
            ))) || delivered;
        }
        return delivered;
    }

    private boolean sendHtmlMail(String recipient,
                                 String subject,
                                 String templateName,
                                 Map<String, Object> variables) {
        try {
            return sendHtmlMailOrThrow(recipient, subject, templateName, variables);
        } catch (Exception ex) {
            log.error("Automated mail delivery failed. recipient={}, subject={}, reason={}",
                    blank(recipient) ? "-" : recipient.trim(), subject, ex.getMessage(), ex);
            return false;
        }
    }

    private boolean sendHtmlMailOrThrow(String recipient,
                                        String subject,
                                        String templateName,
                                        Map<String, Object> variables) throws Exception {
        if (blank(recipient)) {
            log.warn("Skipped automated mail because recipient was blank. subject={}", subject);
            return false;
        }
        if (!isConfigured()) {
            log.warn("Skipped automated mail because mail sender was not configured. recipient={}, subject={}", recipient, subject);
            return false;
        }
        var mimeMessage = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        helper.setTo(recipient.trim());
        helper.setFrom(defaultFromAddress());
        helper.setSubject(subject);
        helper.setText(documentTemplateService.render(templateName, variables), true);
        attachBrandLogo(helper, variables);
        mailSender.send(mimeMessage);
        log.info("Automated mail sent successfully. recipient={}, subject={}", recipient.trim(), subject);
        return true;
    }

    private Map<String, Object> defaultVariables(Map<String, Object> content) {
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.putAll(content);
        Path logoPath = brandingService.getLogoPath();
        boolean logoFileAvailable = Files.exists(logoPath);
        variables.put("logoSrc", logoFileAvailable ? "cid:" + LOGO_CONTENT_ID : brandingService.getLogoDataUri());
        variables.put("logoInlinePath", logoFileAvailable ? logoPath : null);
        variables.put("bankName", brandingService.getBankName());
        variables.put("bankTagline", brandingService.getBankTagline());
        variables.put("supportEmail", properties.getSupportEmail());
        variables.put("supportPhone", properties.getSupportPhone());
        variables.put("currentYear", LocalDateTime.now().getYear());
        return variables;
    }

    private void attachBrandLogo(MimeMessageHelper helper, Map<String, Object> variables) {
        Object logoPath = variables.get("logoInlinePath");
        if (!(logoPath instanceof Path path) || !Files.exists(path)) {
            return;
        }
        try {
            helper.addInline(LOGO_CONTENT_ID, new FileSystemResource(path.toFile()), "image/png");
        } catch (Exception ex) {
            log.warn("Automated mail logo inline attachment skipped. reason={}", ex.getMessage());
        }
    }

    private String otpHeadline(VerificationPurpose purpose) {
        String label = prettyPurpose(purpose);
        return label.endsWith("OTP") ? label : label + " OTP";
    }

    private String prettyPurpose(VerificationPurpose purpose) {
        if (purpose == null) {
            return "Verification";
        }
        String[] words = purpose.name().replace('_', ' ').toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder label = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (!label.isEmpty()) {
                label.append(' ');
            }
            label.append("otp".equals(word) ? "OTP" : Character.toUpperCase(word.charAt(0)) + word.substring(1));
        }
        return label.toString();
    }

    private String safe(String value) {
        return blank(value) ? "-" : value.trim();
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String defaultFromAddress() {
        return blank(verificationDeliveryProperties.getEmail().getFromAddress())
                ? null
                : verificationDeliveryProperties.getEmail().getFromAddress().trim();
    }

    private Iterable<String> resolveOperationalAlertRecipients() {
        var recipients = userRepository.findActiveOperationalAlertEmails().stream()
                .filter(email -> !blank(email))
                .map(String::trim)
                .distinct()
                .toList();
        if (!recipients.isEmpty()) {
            return recipients;
        }
        if (!blank(properties.getSupportEmail())) {
            return java.util.List.of(properties.getSupportEmail().trim());
        }
        return java.util.List.of();
    }
}
