package com.sbms.notification.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public record NotificationEventResponse(
        Long id,
        String eventCode,
        String eventName,
        String referenceModule,
        RecordStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
