package com.sbms.notification.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.notification.enums.NotificationChannelType;

import java.time.LocalDateTime;

public record NotificationTemplateResponse(
        Long id,
        String templateCode,
        String templateName,
        NotificationChannelType channelType,
        String subjectText,
        String bodyText,
        RecordStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
