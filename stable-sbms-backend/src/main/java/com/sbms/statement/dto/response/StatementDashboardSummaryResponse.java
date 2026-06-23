package com.sbms.statement.dto.response;

import java.util.List;

public record StatementDashboardSummaryResponse(
        Long statementsGeneratedToday,
        Long customerStatementRequests,
        Long branchStatementRequests,
        Long exportDownloadCounts,
        List<StatementMetricResponse> mostRequestedStatementTypes,
        List<CustomerStatementRequestResponse> recentCustomerRequests,
        List<BranchStatementRequestResponse> recentBranchRequests
) {
}
