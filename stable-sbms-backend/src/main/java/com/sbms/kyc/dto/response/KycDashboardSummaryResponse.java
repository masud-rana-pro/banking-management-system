package com.sbms.kyc.dto.response;

public class KycDashboardSummaryResponse {

    private Long pendingKyc;
    private Long verifiedKyc;
    private Long rejectedKyc;
    private Long resubmissionQueue;
    private Long highRiskCustomers;
    private Long lowRiskCount;
    private Long mediumRiskCount;
    private Long highRiskCount;

    public KycDashboardSummaryResponse() {
    }

    public KycDashboardSummaryResponse(
            Long pendingKyc,
            Long verifiedKyc,
            Long rejectedKyc,
            Long resubmissionQueue,
            Long highRiskCustomers,
            Long lowRiskCount,
            Long mediumRiskCount,
            Long highRiskCount
    ) {
        this.pendingKyc = pendingKyc;
        this.verifiedKyc = verifiedKyc;
        this.rejectedKyc = rejectedKyc;
        this.resubmissionQueue = resubmissionQueue;
        this.highRiskCustomers = highRiskCustomers;
        this.lowRiskCount = lowRiskCount;
        this.mediumRiskCount = mediumRiskCount;
        this.highRiskCount = highRiskCount;
    }

    public Long getPendingKyc() {
        return pendingKyc;
    }

    public Long getVerifiedKyc() {
        return verifiedKyc;
    }

    public Long getRejectedKyc() {
        return rejectedKyc;
    }

    public Long getResubmissionQueue() {
        return resubmissionQueue;
    }

    public Long getHighRiskCustomers() {
        return highRiskCustomers;
    }

    public Long getLowRiskCount() {
        return lowRiskCount;
    }

    public Long getMediumRiskCount() {
        return mediumRiskCount;
    }

    public Long getHighRiskCount() {
        return highRiskCount;
    }
}
