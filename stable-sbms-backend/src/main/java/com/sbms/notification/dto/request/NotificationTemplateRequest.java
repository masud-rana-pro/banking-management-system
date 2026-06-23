package com.sbms.notification.dto.request;

import com.sbms.notification.enums.NotificationChannelType;

public class NotificationTemplateRequest {

    private String templateCode;
    private String templateName;
    private NotificationChannelType channelType;
    private String subjectText;
    private String bodyText;

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public NotificationChannelType getChannelType() { return channelType; }
    public void setChannelType(NotificationChannelType channelType) { this.channelType = channelType; }
    public String getSubjectText() { return subjectText; }
    public void setSubjectText(String subjectText) { this.subjectText = subjectText; }
    public String getBodyText() { return bodyText; }
    public void setBodyText(String bodyText) { this.bodyText = bodyText; }
}
