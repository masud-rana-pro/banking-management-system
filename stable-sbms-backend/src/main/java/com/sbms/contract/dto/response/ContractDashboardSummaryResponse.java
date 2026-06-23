package com.sbms.contract.dto.response;

import java.util.List;

public class ContractDashboardSummaryResponse {
    private final Long totalContracts;
    private final Long pendingSignatures;
    private final Long activeLockedContracts;
    private final Long contractVersions;
    private final Long draftContracts;
    private final List<ContractResponse> recentContracts;
    private final List<ContractTemplateResponse> recentTemplates;

    public ContractDashboardSummaryResponse(Long totalContracts, Long pendingSignatures, Long activeLockedContracts,
                                            Long contractVersions, Long draftContracts,
                                            List<ContractResponse> recentContracts,
                                            List<ContractTemplateResponse> recentTemplates) {
        this.totalContracts = totalContracts;
        this.pendingSignatures = pendingSignatures;
        this.activeLockedContracts = activeLockedContracts;
        this.contractVersions = contractVersions;
        this.draftContracts = draftContracts;
        this.recentContracts = recentContracts;
        this.recentTemplates = recentTemplates;
    }

    public Long getTotalContracts() { return totalContracts; }
    public Long getPendingSignatures() { return pendingSignatures; }
    public Long getActiveLockedContracts() { return activeLockedContracts; }
    public Long getContractVersions() { return contractVersions; }
    public Long getDraftContracts() { return draftContracts; }
    public List<ContractResponse> getRecentContracts() { return recentContracts; }
    public List<ContractTemplateResponse> getRecentTemplates() { return recentTemplates; }
}
