package com.sbms.report.dto.response;

import com.sbms.customer.enums.RecordStatus;
import com.sbms.report.enums.ReportType;

import java.time.LocalDateTime;

public record ReportDefinitionResponse(
        Long id,
        String reportCode,
        String reportName,
        ReportType reportType,
        String queryKey,
        String exportTypes,
        RecordStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
