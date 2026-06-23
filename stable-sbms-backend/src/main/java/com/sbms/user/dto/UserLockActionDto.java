package com.sbms.user.dto;

public class UserLockActionDto {

    private String actionBy;
    private String reason;

    public String getActionBy() { return actionBy; }
    public void setActionBy(String actionBy) { this.actionBy = actionBy; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
