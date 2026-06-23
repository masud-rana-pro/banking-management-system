package com.sbms.notification.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.notification.enums.NotificationChannelType;
import com.sbms.notification.enums.NotificationDeliveryStatus;

import java.time.LocalDateTime;

public record NotificationLogResponse(
        Long id,
        Long eventId,
        String eventCode,
        String eventName,
        Long templateId,
        String templateCode,
        String templateName,
        String recipientTo,
        NotificationChannelType channelType,
        NotificationDeliveryStatus deliveryStatus,
        String providerResponse,
        Integer retryCount,
        LocalDateTime sentAt,
        RecordStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
