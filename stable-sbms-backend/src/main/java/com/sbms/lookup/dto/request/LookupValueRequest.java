package com.sbms.lookup.dto.request;

public record LookupValueRequest(
        Long lookupTypeId,
        String valueCode,
        String valueLabel,
        String valueBnLabel,
        Integer sortOrder,
        String extraData,
        String status
) {}
