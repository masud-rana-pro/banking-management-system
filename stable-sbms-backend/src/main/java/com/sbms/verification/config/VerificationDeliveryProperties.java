package com.sbms.verification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sbms.verification.delivery")
public class VerificationDeliveryProperties {

    private boolean simulatorFallbackEnabled = true;
    private final Email email = new Email();
    private final Sms sms = new Sms();

    public boolean isSimulatorFallbackEnabled() {
        return simulatorFallbackEnabled;
    }

    public void setSimulatorFallbackEnabled(boolean simulatorFallbackEnabled) {
        this.simulatorFallbackEnabled = simulatorFallbackEnabled;
    }

    public Email getEmail() {
        return email;
    }

    public Sms getSms() {
        return sms;
    }

    public static class Email {
        private boolean enabled;
        private String providerName = "SMTP";
        private String fromAddress;
        private String subjectPrefix = "SBMS";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }

        public String getSubjectPrefix() {
            return subjectPrefix;
        }

        public void setSubjectPrefix(String subjectPrefix) {
            this.subjectPrefix = subjectPrefix;
        }
    }

    public static class Sms {
        private boolean enabled;
        private String providerName = "HTTP_GATEWAY";
        private String endpointUrl;
        private String method = "POST";
        private String authHeaderName = "Authorization";
        private String authHeaderValue;
        private String senderId = "SBMS";
        private String contentType = "application/json";
        private String payloadTemplate = "{\"to\":\"{{to}}\",\"message\":\"{{message}}\",\"senderId\":\"{{senderId}}\"}";
        private String successKeyword = "SUCCESS";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }

        public String getEndpointUrl() {
            return endpointUrl;
        }

        public void setEndpointUrl(String endpointUrl) {
            this.endpointUrl = endpointUrl;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getAuthHeaderName() {
            return authHeaderName;
        }

        public void setAuthHeaderName(String authHeaderName) {
            this.authHeaderName = authHeaderName;
        }

        public String getAuthHeaderValue() {
            return authHeaderValue;
        }

        public void setAuthHeaderValue(String authHeaderValue) {
            this.authHeaderValue = authHeaderValue;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getPayloadTemplate() {
            return payloadTemplate;
        }

        public void setPayloadTemplate(String payloadTemplate) {
            this.payloadTemplate = payloadTemplate;
        }

        public String getSuccessKeyword() {
            return successKeyword;
        }

        public void setSuccessKeyword(String successKeyword) {
            this.successKeyword = successKeyword;
        }
    }
}
