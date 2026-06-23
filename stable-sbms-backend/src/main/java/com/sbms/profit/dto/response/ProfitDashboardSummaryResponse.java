package com.sbms.profit.dto.response;

import java.util.List;

public record ProfitDashboardSummaryResponse(
        Long activeProfitRatios,
        Long pendingPostingCycles,
        Long postedThisMonth,
        Long failedPostingLogs,
        List<ProfitRatioResponse> currentPsrTable,
        UpcomingPostingRunResponse upcomingPostingRun,
        List<ProfitPostingResponse> recentFailedPostings
) {
}
