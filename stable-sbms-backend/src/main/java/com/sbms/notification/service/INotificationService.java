package com.sbms.notification.service;

import com.sbms.notification.dto.request.NotificationEventRequest;
import com.sbms.notification.dto.request.NotificationTemplateRequest;
import com.sbms.notification.dto.response.*;

import java.util.List;

public interface INotificationService {

    NotificationTemplateResponse createTemplate(NotificationTemplateRequest request);
    List<NotificationTemplateResponse> listTemplates();
    NotificationTemplateResponse getTemplateById(Long id);
    NotificationTemplateResponse updateTemplate(Long id, NotificationTemplateRequest request);
    NotificationTemplateResponse archiveTemplate(Long id);
    NotificationTemplateResponse restoreTemplate(Long id);

    NotificationEventResponse createEventRule(NotificationEventRequest request);
    List<NotificationEventResponse> listEventRules();

    List<NotificationLogResponse> listLogs(String deliveryStatus, String channelType, String keyword);
    NotificationLogResponse getLogById(Long id);
    NotificationLogResponse retryLog(Long id);

    NotificationDashboardSummaryResponse getDashboardSummary();
}
