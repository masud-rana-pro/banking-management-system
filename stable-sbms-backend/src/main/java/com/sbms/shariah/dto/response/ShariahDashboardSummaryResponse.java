package com.sbms.shariah.dto.response;

import java.util.List;

public class ShariahDashboardSummaryResponse {
    private final Long pendingCases;
    private final Long approvedCases;
    private final Long rejectedCases;
    private final Long correctionRequests;
    private final Long upcomingReviews;
    private final List<ShariahReviewCaseResponse> recentCases;
    private final List<ShariahReviewDecisionResponse> recentDecisions;
    private final List<ShariahModuleSummaryResponse> moduleBreakdown;

    public ShariahDashboardSummaryResponse(Long pendingCases, Long approvedCases, Long rejectedCases,
                                           Long correctionRequests, Long upcomingReviews,
                                           List<ShariahReviewCaseResponse> recentCases,
                                           List<ShariahReviewDecisionResponse> recentDecisions,
                                           List<ShariahModuleSummaryResponse> moduleBreakdown) {
        this.pendingCases = pendingCases;
        this.approvedCases = approvedCases;
        this.rejectedCases = rejectedCases;
        this.correctionRequests = correctionRequests;
        this.upcomingReviews = upcomingReviews;
        this.recentCases = recentCases;
        this.recentDecisions = recentDecisions;
        this.moduleBreakdown = moduleBreakdown;
    }

    public Long getPendingCases() { return pendingCases; }
    public Long getApprovedCases() { return approvedCases; }
    public Long getRejectedCases() { return rejectedCases; }
    public Long getCorrectionRequests() { return correctionRequests; }
    public Long getUpcomingReviews() { return upcomingReviews; }
    public List<ShariahReviewCaseResponse> getRecentCases() { return recentCases; }
    public List<ShariahReviewDecisionResponse> getRecentDecisions() { return recentDecisions; }
    public List<ShariahModuleSummaryResponse> getModuleBreakdown() { return moduleBreakdown; }
}
