package com.sbms.card.dto.request;

import com.sbms.card.enums.CardPinEventType;

import java.time.LocalDateTime;

public class CardPinEventRequest {

    private CardPinEventType eventType;
    private LocalDateTime eventDate;
    private String performedBy;

    public CardPinEventType getEventType() {
        return eventType;
    }

    public void setEventType(CardPinEventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }
}
