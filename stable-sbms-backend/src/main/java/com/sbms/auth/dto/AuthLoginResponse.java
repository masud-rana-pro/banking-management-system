package com.sbms.auth.dto;

import java.time.LocalDateTime;

public class AuthLoginResponse {

    private Boolean otpRequired;
    private Long otpRequestId;
    private String otpChannelType;
    private String otpDestinationMasked;
    private LocalDateTime otpExpiresAt;
    private AuthSessionResponse session;

    public Boolean getOtpRequired() {
        return otpRequired;
    }

    public void setOtpRequired(Boolean otpRequired) {
        this.otpRequired = otpRequired;
    }

    public Long getOtpRequestId() {
        return otpRequestId;
    }

    public void setOtpRequestId(Long otpRequestId) {
        this.otpRequestId = otpRequestId;
    }

    public String getOtpChannelType() {
        return otpChannelType;
    }

    public void setOtpChannelType(String otpChannelType) {
        this.otpChannelType = otpChannelType;
    }

    public String getOtpDestinationMasked() {
        return otpDestinationMasked;
    }

    public void setOtpDestinationMasked(String otpDestinationMasked) {
        this.otpDestinationMasked = otpDestinationMasked;
    }

    public LocalDateTime getOtpExpiresAt() {
        return otpExpiresAt;
    }

    public void setOtpExpiresAt(LocalDateTime otpExpiresAt) {
        this.otpExpiresAt = otpExpiresAt;
    }


    public AuthSessionResponse getSession() {
        return session;
    }

    public void setSession(AuthSessionResponse session) {
        this.session = session;
    }
}


