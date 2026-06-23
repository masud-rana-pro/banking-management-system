package com.sbms.customer.dto.response;

import java.time.LocalDateTime;

public class CustomerTimelineResponse {

    private String title;
    private String description;
    private String type;
    private LocalDateTime activityTime;

    public CustomerTimelineResponse() {
    }

    public CustomerTimelineResponse(String title, String description, String type, LocalDateTime activityTime) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.activityTime = activityTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public LocalDateTime getActivityTime() {
        return activityTime;
    }
}