package com.sbms.notification.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.notification.controller.INotificationController;
import com.sbms.notification.dto.request.NotificationEventRequest;
import com.sbms.notification.dto.request.NotificationTemplateRequest;
import com.sbms.notification.dto.response.*;
import com.sbms.notification.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("NOTIFICATION_ALERTS_ACCESS")
public class NotificationController implements INotificationController {

    @Autowired
    private INotificationService notificationService;

    @Override
    @RequiresPermission("NOTIFICATION_TEMPLATE_CREATE")
    @PostMapping("/templates/create")
    public ApiResponse<NotificationTemplateResponse> createTemplate(@RequestBody NotificationTemplateRequest request) {
        return ResponseBuilder.success("Notification template created successfully", notificationService.createTemplate(request));
    }

    @Override
    @GetMapping("/templates/list")
    public ApiResponse<List<NotificationTemplateResponse>> listTemplates() {
        return ResponseBuilder.success("Notification templates fetched successfully", notificationService.listTemplates());
    }

    @Override
    @GetMapping("/templates/{id}")
    public ApiResponse<NotificationTemplateResponse> getTemplateById(@PathVariable Long id) {
        return ResponseBuilder.success("Notification template fetched successfully", notificationService.getTemplateById(id));
    }

    @Override
    @RequiresPermission("NOTIFICATION_TEMPLATE_EDIT")
    @PutMapping("/templates/{id}")
    public ApiResponse<NotificationTemplateResponse> updateTemplate(@PathVariable Long id, @RequestBody NotificationTemplateRequest request) {
        return ResponseBuilder.success("Notification template updated successfully", notificationService.updateTemplate(id, request));
    }

    @Override
    @RequiresPermission("NOTIFICATION_TEMPLATE_ARCHIVE")
    @DeleteMapping("/templates/{id}")
    public ApiResponse<NotificationTemplateResponse> archiveTemplate(@PathVariable Long id) {
        return ResponseBuilder.success("Notification template archived successfully", notificationService.archiveTemplate(id));
    }

    @Override
    @RequiresPermission("NOTIFICATION_TEMPLATE_RESTORE")
    @PutMapping("/templates/{id}/restore")
    public ApiResponse<NotificationTemplateResponse> restoreTemplate(@PathVariable Long id) {
        return ResponseBuilder.success("Notification template restored successfully", notificationService.restoreTemplate(id));
    }

    @Override
    @RequiresPermission("NOTIFICATION_EVENT_RULE_CREATE")
    @PostMapping("/event-rules/create")
    public ApiResponse<NotificationEventResponse> createEventRule(@RequestBody NotificationEventRequest request) {
        return ResponseBuilder.success("Notification event rule created successfully", notificationService.createEventRule(request));
    }

    @Override
    @GetMapping("/event-rules/list")
    public ApiResponse<List<NotificationEventResponse>> listEventRules() {
        return ResponseBuilder.success("Notification event rules fetched successfully", notificationService.listEventRules());
    }

    @Override
    @GetMapping("/logs/list")
    public ApiResponse<List<NotificationLogResponse>> listLogs(
            @RequestParam(required = false) String deliveryStatus,
            @RequestParam(required = false) String channelType,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Notification logs fetched successfully", notificationService.listLogs(deliveryStatus, channelType, keyword));
    }

    @Override
    @GetMapping("/logs/{id}")
    public ApiResponse<NotificationLogResponse> getLogById(@PathVariable Long id) {
        return ResponseBuilder.success("Notification log fetched successfully", notificationService.getLogById(id));
    }

    @Override
    @RequiresPermission("NOTIFICATION_RETRY")
    @PostMapping("/logs/{id}/retry")
    public ApiResponse<NotificationLogResponse> retryLog(@PathVariable Long id) {
        return ResponseBuilder.success("Notification retry queued successfully", notificationService.retryLog(id));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<NotificationDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Notification dashboard summary fetched successfully", notificationService.getDashboardSummary());
    }
}
