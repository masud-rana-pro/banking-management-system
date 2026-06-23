package com.sbms.account.dto.response;

import com.sbms.account.enums.ShariahContractType;
import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountTypeResponse(
        Long id,
        String typeCode,
        String typeName,
        ShariahContractType shariahContractType,
        String currencyCode,
        BigDecimal minimumOpeningBalance,
        Boolean profitApplicable,
        Boolean withdrawalAllowed,
        RecordStatus status,
        LocalDateTime createdAt
) {
}
