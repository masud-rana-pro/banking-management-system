package com.sbms.financing.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class FinancingRepaymentCollectionResponse {

    private final Long applicationId;
    private final String applicationNo;
    private final BigDecimal collectedAmount;
    private final BigDecimal remainingOutstandingAmount;
    private final String applicationStatus;
    private final List<FinancingScheduleResponse> updatedSchedules;

    public FinancingRepaymentCollectionResponse(Long applicationId, String applicationNo,
                                                BigDecimal collectedAmount,
                                                BigDecimal remainingOutstandingAmount,
                                                String applicationStatus,
                                                List<FinancingScheduleResponse> updatedSchedules) {
        this.applicationId = applicationId;
        this.applicationNo = applicationNo;
        this.collectedAmount = collectedAmount;
        this.remainingOutstandingAmount = remainingOutstandingAmount;
        this.applicationStatus = applicationStatus;
        this.updatedSchedules = updatedSchedules;
    }

    public Long getApplicationId() { return applicationId; }
    public String getApplicationNo() { return applicationNo; }
    public BigDecimal getCollectedAmount() { return collectedAmount; }
    public BigDecimal getRemainingOutstandingAmount() { return remainingOutstandingAmount; }
    public String getApplicationStatus() { return applicationStatus; }
    public List<FinancingScheduleResponse> getUpdatedSchedules() { return updatedSchedules; }
}
