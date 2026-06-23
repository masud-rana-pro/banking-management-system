package com.sbms.closing.dto.response;

public record MonthlyClosingDashboardSummaryResponse(
        long draftCount,
        long submittedCount,
        long approvedCount,
        long rejectedCount,
        long reopenedCount
) {
}
