package com.sbms.profit.dto.request;

import com.sbms.profit.enums.ProfitFrequency;

import java.time.LocalDate;

public class ProfitScheduleRequest {

    private Long accountId;
    private ProfitFrequency profitFrequency;
    private LocalDate nextPostingDate;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public ProfitFrequency getProfitFrequency() {
        return profitFrequency;
    }

    public void setProfitFrequency(ProfitFrequency profitFrequency) {
        this.profitFrequency = profitFrequency;
    }

    public LocalDate getNextPostingDate() {
        return nextPostingDate;
    }

    public void setNextPostingDate(LocalDate nextPostingDate) {
        this.nextPostingDate = nextPostingDate;
    }
}
