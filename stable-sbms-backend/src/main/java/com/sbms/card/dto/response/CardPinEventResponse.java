package com.sbms.card.dto.response;

import com.sbms.card.enums.CardPinEventType;
import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public record CardPinEventResponse(
        Long id,
        Long cardId,
        String cardRefNo,
        CardPinEventType eventType,
        LocalDateTime eventDate,
        String performedBy,
        RecordStatus status,
        LocalDateTime createdAt
) {
}
