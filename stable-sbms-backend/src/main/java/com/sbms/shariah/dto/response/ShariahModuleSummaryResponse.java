package com.sbms.shariah.dto.response;

public class ShariahModuleSummaryResponse {
    private final String referenceModule;
    private final Long totalCases;

    public ShariahModuleSummaryResponse(String referenceModule, Long totalCases) {
        this.referenceModule = referenceModule;
        this.totalCases = totalCases;
    }

    public String getReferenceModule() { return referenceModule; }
    public Long getTotalCases() { return totalCases; }
}
