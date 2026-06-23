package com.sbms.account.dto.response;

public record AccountTypeDropdownResponse(
        Long id,
        String typeCode,
        String typeName,
        String displayName
) {
}
