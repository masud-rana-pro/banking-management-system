package com.sbms.report.dto.response;

import java.math.BigDecimal;

public record ReportRowResponse(
        String primaryLabel,
        String secondaryLabel,
        String tertiaryLabel,
        BigDecimal amountOne,
        BigDecimal amountTwo,
        BigDecimal amountThree,
        String status
) {}
