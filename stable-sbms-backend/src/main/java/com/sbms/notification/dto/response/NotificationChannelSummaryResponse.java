package com.sbms.notification.dto.response;

import com.sbms.notification.enums.NotificationChannelType;

public record NotificationChannelSummaryResponse(
        NotificationChannelType channelType,
        long sentCount,
        long failedCount,
        long retryQueuedCount
) {}
