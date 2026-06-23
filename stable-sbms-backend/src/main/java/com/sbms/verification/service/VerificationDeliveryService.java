package com.sbms.verification.service;

import com.sbms.common.mail.AutomatedMailService;
import com.sbms.verification.config.VerificationDeliveryProperties;
import com.sbms.verification.entity.VerificationTemplate;
import com.sbms.verification.enums.ChannelType;
import com.sbms.verification.enums.VerificationPurpose;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
public class VerificationDeliveryService {

    private final VerificationDeliveryProperties properties;
    private final JavaMailSender mailSender;
    private final RestClient restClient;
    private final AutomatedMailService automatedMailService;

    public VerificationDeliveryService(
            VerificationDeliveryProperties properties,
            ObjectProvider<JavaMailSender> mailSenderProvider,
            AutomatedMailService automatedMailService
    ) {
        this.properties = properties;
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.restClient = RestClient.builder().build();
        this.automatedMailService = automatedMailService;
    }

    public OtpDeliveryResult deliver(ChannelType channelType, String contactValue, VerificationPurpose purpose,
                                     String otpCode, String remarks, VerificationTemplate template) {
        return switch (channelType) {
            case EMAIL -> deliverEmail(contactValue, purpose, otpCode, remarks, template);
            case SMS -> deliverSms(contactValue, purpose, otpCode, remarks, template);
        };
    }

    @Async("verificationDeliveryExecutor")
    public CompletableFuture<OtpDeliveryResult> deliverAsync(ChannelType channelType, String contactValue,
                                                             VerificationPurpose purpose, String otpCode,
                                                             String remarks, VerificationTemplate template) {
        return CompletableFuture.completedFuture(deliver(channelType, contactValue, purpose, otpCode, remarks, template));
    }
    private OtpDeliveryResult deliverEmail(String recipient, VerificationPurpose purpose, String otpCode,
                                           String remarks, VerificationTemplate template) {
        if (!isEmailConfigured()) {
            if (properties.isSimulatorFallbackEnabled()) {
                return simulator("EMAIL_SIMULATOR", ChannelType.EMAIL, purpose, otpCode, remarks, template);
            }
            return new OtpDeliveryResult(
                    false,
                    normalizedProviderName(properties.getEmail().getProviderName(), "SMTP"),
                    "provider=" + normalizedProviderName(properties.getEmail().getProviderName(), "SMTP")
                            + "; channel=EMAIL; status=FAILED; reason=Email provider is not configured"
            );
        }

        try {
            boolean delivered = automatedMailService.sendOtpEmail(
                    recipient,
                    resolveEmailSubject(template, purpose),
                    purpose,
                    otpCode,
                    5,
                    remarks
            );
            if (!delivered) {
                if (properties.isSimulatorFallbackEnabled()) {
                    return simulator("EMAIL_SIMULATOR", ChannelType.EMAIL, purpose, otpCode,
                            "SMTP delivery failed, simulator fallback used.", template);
                }
                return new OtpDeliveryResult(
                        false,
                        normalizedProviderName(properties.getEmail().getProviderName(), "SMTP"),
                        "provider=" + normalizedProviderName(properties.getEmail().getProviderName(), "SMTP")
                                + "; channel=EMAIL; status=FAILED; reason=SMTP send did not complete successfully"
                );
            }
            return new OtpDeliveryResult(
                    true,
                    normalizedProviderName(properties.getEmail().getProviderName(), "SMTP"),
                    "provider=" + normalizedProviderName(properties.getEmail().getProviderName(), "SMTP")
                            + "; channel=EMAIL; recipient=" + recipient
            );
        } catch (Exception ex) {
            if (properties.isSimulatorFallbackEnabled()) {
                return simulator("EMAIL_SIMULATOR", ChannelType.EMAIL, purpose, otpCode,
                        "SMTP delivery failed, simulator fallback used. " + safeReason(ex), template);
            }
            return new OtpDeliveryResult(
                    false,
                    normalizedProviderName(properties.getEmail().getProviderName(), "SMTP"),
                    "provider=" + normalizedProviderName(properties.getEmail().getProviderName(), "SMTP")
                            + "; channel=EMAIL; status=FAILED; reason=" + safeReason(ex)
            );
        }
    }

    private OtpDeliveryResult deliverSms(String recipient, VerificationPurpose purpose, String otpCode,
                                         String remarks, VerificationTemplate template) {
        if (!isSmsConfigured()) {
            if (properties.isSimulatorFallbackEnabled()) {
                return simulator("SMS_SIMULATOR", ChannelType.SMS, purpose, otpCode, remarks, template);
            }
            return new OtpDeliveryResult(
                    false,
                    normalizedProviderName(properties.getSms().getProviderName(), "HTTP_GATEWAY"),
                    "provider=" + normalizedProviderName(properties.getSms().getProviderName(), "HTTP_GATEWAY")
                            + "; channel=SMS; status=FAILED; reason=SMS provider is not configured"
            );
        }

        String message = renderMessage(template, ChannelType.SMS, purpose, otpCode, remarks);
        String payloadTemplate = blank(properties.getSms().getPayloadTemplate())
                ? "{\"to\":\"{{to}}\",\"message\":\"{{message}}\",\"senderId\":\"{{senderId}}\"}"
                : properties.getSms().getPayloadTemplate();
        String payload = payloadTemplate
                .replace("{{to}}", escapeJson(recipient))
                .replace("{{message}}", escapeJson(message))
                .replace("{{senderId}}", escapeJson(blank(properties.getSms().getSenderId()) ? "SBMS" : properties.getSms().getSenderId().trim()))
                .replace("{{otp}}", escapeJson(otpCode));

        try {
            String responseBody = restClient.method(resolveHttpMethod(properties.getSms().getMethod()))
                    .uri(properties.getSms().getEndpointUrl().trim())
                    .headers(headers -> {
                        headers.setContentType(MediaType.parseMediaType(blank(properties.getSms().getContentType())
                                ? MediaType.APPLICATION_JSON_VALUE : properties.getSms().getContentType().trim()));
                        if (!blank(properties.getSms().getAuthHeaderName()) && !blank(properties.getSms().getAuthHeaderValue())) {
                            headers.set(properties.getSms().getAuthHeaderName().trim(), properties.getSms().getAuthHeaderValue().trim());
                        }
                    })
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            boolean success = blank(properties.getSms().getSuccessKeyword())
                    || (responseBody != null && responseBody.toUpperCase(Locale.ROOT)
                    .contains(properties.getSms().getSuccessKeyword().trim().toUpperCase(Locale.ROOT)));

            if (success) {
                return new OtpDeliveryResult(
                        true,
                        normalizedProviderName(properties.getSms().getProviderName(), "HTTP_GATEWAY"),
                        "provider=" + normalizedProviderName(properties.getSms().getProviderName(), "HTTP_GATEWAY")
                                + "; channel=SMS; recipient=" + recipient
                                + "; response=" + truncate(responseBody)
                );
            }

            if (properties.isSimulatorFallbackEnabled()) {
                return simulator("SMS_SIMULATOR", ChannelType.SMS, purpose, otpCode,
                        "SMS gateway did not return configured success marker. " + truncate(responseBody), template);
            }

            return new OtpDeliveryResult(
                    false,
                    normalizedProviderName(properties.getSms().getProviderName(), "HTTP_GATEWAY"),
                    "provider=" + normalizedProviderName(properties.getSms().getProviderName(), "HTTP_GATEWAY")
                            + "; channel=SMS; status=FAILED; reason=Missing success marker; response=" + truncate(responseBody)
            );
        } catch (Exception ex) {
            if (properties.isSimulatorFallbackEnabled()) {
                return simulator("SMS_SIMULATOR", ChannelType.SMS, purpose, otpCode,
                        "SMS delivery failed, simulator fallback used. " + safeReason(ex), template);
            }
            return new OtpDeliveryResult(
                    false,
                    normalizedProviderName(properties.getSms().getProviderName(), "HTTP_GATEWAY"),
                    "provider=" + normalizedProviderName(properties.getSms().getProviderName(), "HTTP_GATEWAY")
                            + "; channel=SMS; status=FAILED; reason=" + safeReason(ex)
            );
        }
    }

    private OtpDeliveryResult simulator(String providerName, ChannelType channelType, VerificationPurpose purpose,
                                        String otpCode, String remarks, VerificationTemplate template) {
        return new OtpDeliveryResult(
                true,
                providerName,
                "provider=" + providerName + "; channel=" + channelType.name()
                        + "; purpose=" + purpose.name()
                        + "; template=" + (template == null ? "LOCAL_TEMPLATE" : template.getTemplateName())
                        + "; otpPreview=" + otpCode
                        + (blank(remarks) ? "" : "; remarks=" + remarks.trim())
        );
    }

    private String resolveEmailSubject(VerificationTemplate template, VerificationPurpose purpose) {
        String subject = template == null || blank(template.getSubjectLine())
                ? "Your " + prettyPurpose(purpose) + " OTP"
                : template.getSubjectLine().trim();
        if (blank(properties.getEmail().getSubjectPrefix())) {
            return subject;
        }
        return properties.getEmail().getSubjectPrefix().trim() + " | " + subject;
    }

    private String renderMessage(VerificationTemplate template, ChannelType channelType,
                                 VerificationPurpose purpose, String otpCode, String remarks) {
        String base = template == null || blank(template.getTemplateBody())
                ? defaultMessage(channelType, purpose)
                : template.getTemplateBody().trim();
        return base
                .replace("{{otp}}", otpCode)
                .replace("{{purpose}}", prettyPurpose(purpose))
                .replace("{{channel}}", channelType.name())
                .replace("{{expiresMinutes}}", "5")
                .replace("{{remarks}}", blank(remarks) ? "" : remarks.trim())
                .replace("{{appName}}", "SBMS")
                .replace("{{contact}}", channelType == ChannelType.EMAIL ? "email" : "mobile");
    }

    private String defaultMessage(ChannelType channelType, VerificationPurpose purpose) {
        return "Your " + prettyPurpose(purpose) + " OTP is {{otp}}. It will expire in {{expiresMinutes}} minutes.";
    }

    private String prettyPurpose(VerificationPurpose purpose) {
        return purpose == null ? "verification" : purpose.name().replace('_', ' ').toLowerCase(Locale.ROOT);
    }

    private boolean isEmailConfigured() {
        return properties.getEmail().isEnabled()
                && mailSender != null
                && automatedMailService.isConfigured();
    }

    private boolean isSmsConfigured() {
        return properties.getSms().isEnabled()
                && !blank(properties.getSms().getEndpointUrl());
    }

    private HttpMethod resolveHttpMethod(String method) {
        if (blank(method)) {
            return HttpMethod.POST;
        }
        try {
            return HttpMethod.valueOf(method.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            return HttpMethod.POST;
        }
    }

    private String normalizedProviderName(String value, String fallback) {
        return blank(value) ? fallback : value.trim();
    }

    private String safeReason(Exception ex) {
        if (ex == null || blank(ex.getMessage())) {
            return "Unknown provider error";
        }
        return truncate(ex.getMessage());
    }

    private String truncate(String value) {
        if (blank(value)) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.length() > 300 ? trimmed.substring(0, 300) : trimmed;
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

