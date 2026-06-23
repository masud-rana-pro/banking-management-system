package com.sbms.statement.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.time.LocalDateTime;

public record FileReferenceResponse(
        Long id,
        String fileName,
        String originalFileName,
        String filePath,
        String fileType,
        Long fileSize,
        String moduleName,
        String referenceTable,
        Long referenceId,
        RecordStatus status,
        LocalDateTime createdAt
) {
}
