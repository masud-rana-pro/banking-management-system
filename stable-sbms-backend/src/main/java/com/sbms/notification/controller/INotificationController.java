package com.sbms.notification.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.notification.dto.request.NotificationEventRequest;
import com.sbms.notification.dto.request.NotificationTemplateRequest;
import com.sbms.notification.dto.response.*;

import java.util.List;

public interface INotificationController {

    ApiResponse<NotificationTemplateResponse> createTemplate(NotificationTemplateRequest request);
    ApiResponse<List<NotificationTemplateResponse>> listTemplates();
    ApiResponse<NotificationTemplateResponse> getTemplateById(Long id);
    ApiResponse<NotificationTemplateResponse> updateTemplate(Long id, NotificationTemplateRequest request);
    ApiResponse<NotificationTemplateResponse> archiveTemplate(Long id);
    ApiResponse<NotificationTemplateResponse> restoreTemplate(Long id);

    ApiResponse<NotificationEventResponse> createEventRule(NotificationEventRequest request);
    ApiResponse<List<NotificationEventResponse>> listEventRules();

    ApiResponse<List<NotificationLogResponse>> listLogs(String deliveryStatus, String channelType, String keyword);
    ApiResponse<NotificationLogResponse> getLogById(Long id);
    ApiResponse<NotificationLogResponse> retryLog(Long id);

    ApiResponse<NotificationDashboardSummaryResponse> dashboardSummary();
}
