package com.sbms.depositscheme.dto.response;

import java.util.List;

public class DepositSchemeDashboardSummaryResponse {

    private final Long totalSchemes;
    private final Long activeEnrollments;
    private final Long dueInstallments;
    private final Long earlyWithdrawalRequests;
    private final Long maturedSchemes;
    private final List<DepositSchemeResponse> recentSchemes;
    private final List<DepositSchemeEnrollmentResponse> recentEnrollments;

    public DepositSchemeDashboardSummaryResponse(Long totalSchemes, Long activeEnrollments, Long dueInstallments,
                                                 Long earlyWithdrawalRequests, Long maturedSchemes,
                                                 List<DepositSchemeResponse> recentSchemes,
                                                 List<DepositSchemeEnrollmentResponse> recentEnrollments) {
        this.totalSchemes = totalSchemes;
        this.activeEnrollments = activeEnrollments;
        this.dueInstallments = dueInstallments;
        this.earlyWithdrawalRequests = earlyWithdrawalRequests;
        this.maturedSchemes = maturedSchemes;
        this.recentSchemes = recentSchemes;
        this.recentEnrollments = recentEnrollments;
    }

    public Long getTotalSchemes() { return totalSchemes; }
    public Long getActiveEnrollments() { return activeEnrollments; }
    public Long getDueInstallments() { return dueInstallments; }
    public Long getEarlyWithdrawalRequests() { return earlyWithdrawalRequests; }
    public Long getMaturedSchemes() { return maturedSchemes; }
    public List<DepositSchemeResponse> getRecentSchemes() { return recentSchemes; }
    public List<DepositSchemeEnrollmentResponse> getRecentEnrollments() { return recentEnrollments; }
}
