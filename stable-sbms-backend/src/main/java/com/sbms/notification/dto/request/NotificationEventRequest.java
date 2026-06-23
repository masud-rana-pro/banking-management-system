package com.sbms.notification.dto.request;

public class NotificationEventRequest {

    private String eventCode;
    private String eventName;
    private String referenceModule;

    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getReferenceModule() { return referenceModule; }
    public void setReferenceModule(String referenceModule) { this.referenceModule = referenceModule; }
}
