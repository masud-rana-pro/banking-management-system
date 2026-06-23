package com.sbms.lookup.dto.response;

import java.time.LocalDateTime;

public record LookupTypeResponse(
        Long id,
        String typeCode,
        String typeName,
        String description,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long activeValueCount,
        long totalValueCount
) {}
