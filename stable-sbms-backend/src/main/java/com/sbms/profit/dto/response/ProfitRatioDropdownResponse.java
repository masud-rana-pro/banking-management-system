package com.sbms.profit.dto.response;

public record ProfitRatioDropdownResponse(
        Long id,
        String ratioCode,
        Long accountTypeId,
        String accountTypeName
) {
}
