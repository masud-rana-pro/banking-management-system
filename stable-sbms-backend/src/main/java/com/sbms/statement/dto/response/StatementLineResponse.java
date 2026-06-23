package com.sbms.statement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StatementLineResponse(
        LocalDate lineDate,
        LocalDateTime lineDateTime,
        String lineType,
        String referenceNo,
        String channel,
        BigDecimal debitAmount,
        BigDecimal creditAmount,
        String narration
) {
}
