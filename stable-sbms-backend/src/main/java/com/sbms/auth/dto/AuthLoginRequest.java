package com.sbms.auth.dto;

public class AuthLoginRequest {

    private String username;
    private String password;
    private Boolean rememberMe;
    private String otpChannelPreference;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public String getOtpChannelPreference() {
        return otpChannelPreference;
    }

    public void setOtpChannelPreference(String otpChannelPreference) {
        this.otpChannelPreference = otpChannelPreference;
    }
}
