package com.sbms.notification.service.impl;

import com.sbms.common.exception.BadRequestException;
import com.sbms.common.exception.ResourceNotFoundException;
import com.sbms.common.mail.AutomatedMailService;
import com.sbms.common.websocket.LiveUpdateGateway;
import com.sbms.customer.enums.RecordStatus;
import com.sbms.notification.dto.request.NotificationEventRequest;
import com.sbms.notification.dto.request.NotificationTemplateRequest;
import com.sbms.notification.dto.response.*;
import com.sbms.notification.entity.NotificationEvent;
import com.sbms.notification.entity.NotificationLog;
import com.sbms.notification.entity.NotificationTemplate;
import com.sbms.notification.enums.NotificationChannelType;
import com.sbms.notification.enums.NotificationDeliveryStatus;
import com.sbms.notification.repository.NotificationEventRepository;
import com.sbms.notification.repository.NotificationLogRepository;
import com.sbms.notification.repository.NotificationTemplateRepository;
import com.sbms.notification.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class NotificationService implements INotificationService {

    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    private NotificationEventRepository eventRepository;

    @Autowired
    private NotificationLogRepository logRepository;

    @Autowired
    private LiveUpdateGateway liveUpdateGateway;

    @Autowired
    private AutomatedMailService automatedMailService;

    @Override
    public NotificationTemplateResponse createTemplate(NotificationTemplateRequest request) {
        validateTemplate(request, null);
        NotificationTemplate entity = new NotificationTemplate();
        entity.setTemplateCode(resolveTemplateCode(request.getTemplateCode()));
        applyTemplate(entity, request);
        entity.setStatus(RecordStatus.ACTIVE);
        NotificationTemplateResponse response = mapTemplate(templateRepository.save(entity));
        publishNotificationEvent("Template Created", "Notification template " + response.templateName() + " was created.", "INFO");
        return response;
    }

    @Override
    public List<NotificationTemplateResponse> listTemplates() {
        return templateRepository.findAll().stream().map(this::mapTemplate).toList();
    }

    @Override
    public NotificationTemplateResponse getTemplateById(Long id) {
        return mapTemplate(getTemplateEntity(id));
    }

    @Override
    public NotificationTemplateResponse updateTemplate(Long id, NotificationTemplateRequest request) {
        NotificationTemplate entity = getTemplateEntity(id);
        validateTemplate(request, id);
        if (request.getTemplateCode() != null && !request.getTemplateCode().trim().isEmpty()) {
            entity.setTemplateCode(request.getTemplateCode().trim().toUpperCase());
        }
        applyTemplate(entity, request);
        return mapTemplate(templateRepository.update(entity));
    }

    @Override
    public NotificationTemplateResponse archiveTemplate(Long id) {
        NotificationTemplate entity = getTemplateEntity(id);
        entity.setStatus(RecordStatus.ARCHIVED);
        return mapTemplate(templateRepository.update(entity));
    }

    @Override
    public NotificationTemplateResponse restoreTemplate(Long id) {
        NotificationTemplate entity = getTemplateEntity(id);
        entity.setStatus(RecordStatus.ACTIVE);
        return mapTemplate(templateRepository.update(entity));
    }

    @Override
    public NotificationEventResponse createEventRule(NotificationEventRequest request) {
        validateEvent(request, null);
        NotificationEvent entity = new NotificationEvent();
        entity.setEventCode(resolveEventCode(request.getEventCode()));
        applyEvent(entity, request);
        entity.setStatus(RecordStatus.ACTIVE);
        NotificationEventResponse response = mapEvent(eventRepository.save(entity));
        publishNotificationEvent("Event Rule Created", "Notification event rule " + response.eventName() + " was created.", "SUCCESS");
        return response;
    }

    @Override
    public List<NotificationEventResponse> listEventRules() {
        return eventRepository.findAll().stream().map(this::mapEvent).toList();
    }

    @Override
    public List<NotificationLogResponse> listLogs(String deliveryStatus, String channelType, String keyword) {
        return logRepository.findAll(deliveryStatus, channelType, keyword).stream().map(this::mapLog).toList();
    }

    @Override
    public NotificationLogResponse getLogById(Long id) {
        return mapLog(getLogEntity(id));
    }

    @Override
    public NotificationLogResponse retryLog(Long id) {
        NotificationLog entity = getLogEntity(id);
        entity.setRetryCount((entity.getRetryCount() == null ? 0 : entity.getRetryCount()) + 1);
        entity.setDeliveryStatus(NotificationDeliveryStatus.RETRY_QUEUED);
        entity.setProviderResponse("Manual retry queued on " + LocalDateTime.now());
        NotificationLogResponse response = mapLog(logRepository.update(entity));
        publishNotificationEvent("Retry Queued", "Notification retry was queued for " + response.recipientTo() + ".", "WARNING");
        automatedMailService.sendOperationalAlertToSupport(
                "Notification retry queued",
                "A notification delivery has been pushed into the retry queue.",
                "Recipient: " + safe(response.recipientTo()) + "<br>Channel: " + response.channelType() + "<br>Event: " + safe(response.eventName()),
                "/notifications/dashboard",
                "Open Notifications"
        );
        return response;
    }

    private void publishNotificationEvent(String title, String message, String severity) {
        liveUpdateGateway.publish(
                "NOTIFICATION",
                title,
                message,
                severity,
                "/notifications/dashboard",
                null,
                null,
                "NOTIFICATION_ALERTS_ACCESS"
        );
    }

    @Override
    public NotificationDashboardSummaryResponse getDashboardSummary() {
        List<NotificationChannelSummaryResponse> channelSummary = Arrays.stream(NotificationChannelType.values())
                .map(channelType -> new NotificationChannelSummaryResponse(
                        channelType,
                        logRepository.countByChannelAndStatus(channelType, NotificationDeliveryStatus.SENT),
                        logRepository.countByChannelAndStatus(channelType, NotificationDeliveryStatus.FAILED),
                        logRepository.countByChannelAndStatus(channelType, NotificationDeliveryStatus.RETRY_QUEUED)
                ))
                .toList();

        return new NotificationDashboardSummaryResponse(
                logRepository.countByStatusForToday(NotificationDeliveryStatus.SENT),
                logRepository.countByStatusForToday(NotificationDeliveryStatus.FAILED),
                logRepository.countByStatus(NotificationDeliveryStatus.RETRY_QUEUED),
                logRepository.countByStatusForToday(NotificationDeliveryStatus.SENT),
                logRepository.countByStatus(NotificationDeliveryStatus.FAILED),
                channelSummary,
                logRepository.findRecent(10).stream().map(this::mapLog).toList()
        );
    }

    private void validateTemplate(NotificationTemplateRequest request, Long existingId) {
        if (request == null) throw new BadRequestException("Notification template request is required");
        if (request.getTemplateName() == null || request.getTemplateName().trim().isEmpty()) throw new BadRequestException("Template name is required");
        if (request.getChannelType() == null) throw new BadRequestException("Channel type is required");
        if (request.getBodyText() == null || request.getBodyText().trim().isEmpty()) throw new BadRequestException("Body text is required");
        if (request.getTemplateCode() != null && !request.getTemplateCode().trim().isEmpty()) {
            templateRepository.findByTemplateCode(request.getTemplateCode().trim())
                    .filter(item -> existingId == null || !item.getId().equals(existingId))
                    .ifPresent(item -> { throw new BadRequestException("Template code already exists"); });
        }
    }

    private void validateEvent(NotificationEventRequest request, Long existingId) {
        if (request == null) throw new BadRequestException("Notification event request is required");
        if (request.getEventName() == null || request.getEventName().trim().isEmpty()) throw new BadRequestException("Event name is required");
        if (request.getEventCode() != null && !request.getEventCode().trim().isEmpty()) {
            eventRepository.findByEventCode(request.getEventCode().trim())
                    .filter(item -> existingId == null || !item.getId().equals(existingId))
                    .ifPresent(item -> { throw new BadRequestException("Event code already exists"); });
        }
    }

    private NotificationTemplate getTemplateEntity(Long id) {
        if (id == null) throw new BadRequestException("Template id is required");
        return templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification template not found"));
    }

    private NotificationLog getLogEntity(Long id) {
        if (id == null) throw new BadRequestException("Notification log id is required");
        return logRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification log not found"));
    }

    private String resolveTemplateCode(String requested) {
        if (requested != null && !requested.trim().isEmpty()) return requested.trim().toUpperCase();
        String last = templateRepository.findLastTemplateCode();
        int next = 1;
        if (last != null && last.matches("NTF-\\d+")) next = Integer.parseInt(last.substring(4)) + 1;
        return String.format("NTF-%05d", next);
    }

    private String resolveEventCode(String requested) {
        if (requested != null && !requested.trim().isEmpty()) return requested.trim().toUpperCase();
        String last = eventRepository.findLastEventCode();
        int next = 1;
        if (last != null && last.matches("EVT-\\d+")) next = Integer.parseInt(last.substring(4)) + 1;
        return String.format("EVT-%05d", next);
    }

    private void applyTemplate(NotificationTemplate entity, NotificationTemplateRequest request) {
        entity.setTemplateName(request.getTemplateName().trim());
        entity.setChannelType(request.getChannelType());
        entity.setSubjectText(request.getSubjectText() == null ? null : request.getSubjectText().trim());
        entity.setBodyText(request.getBodyText().trim());
    }

    private void applyEvent(NotificationEvent entity, NotificationEventRequest request) {
        entity.setEventName(request.getEventName().trim());
        entity.setReferenceModule(request.getReferenceModule() == null ? null : request.getReferenceModule().trim().toUpperCase());
    }

    private NotificationTemplateResponse mapTemplate(NotificationTemplate entity) {
        return new NotificationTemplateResponse(
                entity.getId(),
                entity.getTemplateCode(),
                entity.getTemplateName(),
                entity.getChannelType(),
                entity.getSubjectText(),
                entity.getBodyText(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private NotificationEventResponse mapEvent(NotificationEvent entity) {
        return new NotificationEventResponse(
                entity.getId(),
                entity.getEventCode(),
                entity.getEventName(),
                entity.getReferenceModule(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private NotificationLogResponse mapLog(NotificationLog entity) {
        return new NotificationLogResponse(
                entity.getId(),
                entity.getEvent().getId(),
                entity.getEvent().getEventCode(),
                entity.getEvent().getEventName(),
                entity.getTemplate().getId(),
                entity.getTemplate().getTemplateCode(),
                entity.getTemplate().getTemplateName(),
                entity.getRecipientTo(),
                entity.getChannelType(),
                entity.getDeliveryStatus(),
                entity.getProviderResponse(),
                entity.getRetryCount(),
                entity.getSentAt(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
