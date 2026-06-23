package com.sbms.card.dto.response;

import com.sbms.card.enums.CardEventType;

import java.time.LocalDateTime;

public record CardTransactionResponse(
        Long eventId,
        Long cardId,
        String cardRefNo,
        String maskedCardNo,
        Long customerId,
        String customerCode,
        String customerName,
        Long accountId,
        String accountNumber,
        CardEventType eventType,
        LocalDateTime eventDate,
        String performedBy,
        String remarks
) {
}
