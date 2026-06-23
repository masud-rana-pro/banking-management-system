package com.sbms.card.dto.response;

import com.sbms.card.enums.CardEventType;
import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public record CardEventLogResponse(
        Long id,
        Long cardId,
        String cardRefNo,
        CardEventType eventType,
        LocalDateTime eventDate,
        String performedBy,
        String remarks,
        RecordStatus status,
        LocalDateTime createdAt
) {
}
