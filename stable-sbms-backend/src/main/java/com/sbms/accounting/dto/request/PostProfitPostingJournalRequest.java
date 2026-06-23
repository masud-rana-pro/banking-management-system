package com.sbms.accounting.dto.request;

public class PostProfitPostingJournalRequest {
    private String liabilityAccountCode;

    public String getLiabilityAccountCode() {
        return liabilityAccountCode;
    }

    public void setLiabilityAccountCode(String liabilityAccountCode) {
        this.liabilityAccountCode = liabilityAccountCode;
    }
}
