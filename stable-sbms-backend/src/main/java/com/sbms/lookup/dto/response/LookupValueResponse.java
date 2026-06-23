package com.sbms.lookup.dto.response;

import java.time.LocalDateTime;

public record LookupValueResponse(
        Long id,
        Long lookupTypeId,
        String typeCode,
        String typeName,
        String valueCode,
        String valueLabel,
        String valueBnLabel,
        Integer sortOrder,
        String extraData,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
