package com.sbms.notification.dto.response;

import java.util.List;

public record NotificationDashboardSummaryResponse(
        long sentToday,
        long failedToday,
        long retryQueue,
        long messagesSentToday,
        long failedDeliveries,
        List<NotificationChannelSummaryResponse> channelWiseSummary,
        List<NotificationLogResponse> recentLogs
) {}
