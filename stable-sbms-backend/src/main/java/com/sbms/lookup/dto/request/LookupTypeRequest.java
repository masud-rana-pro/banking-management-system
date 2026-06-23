package com.sbms.lookup.dto.request;

public record LookupTypeRequest(
        String typeCode,
        String typeName,
        String description,
        String status
) {}
