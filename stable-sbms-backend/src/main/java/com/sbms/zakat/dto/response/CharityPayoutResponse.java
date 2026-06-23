package com.sbms.zakat.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CharityPayoutResponse {
    private final Long id;
    private final Long beneficiaryId;
    private final String beneficiaryCode;
    private final String beneficiaryName;
    private final LocalDate payoutDate;
    private final BigDecimal amount;
    private final String approvedBy;
    private final String remarks;
    private final RecordStatus status;
    private final LocalDateTime createdAt;

    public CharityPayoutResponse(Long id, Long beneficiaryId, String beneficiaryCode, String beneficiaryName,
                                 LocalDate payoutDate, BigDecimal amount, String approvedBy, String remarks,
                                 RecordStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.beneficiaryId = beneficiaryId;
        this.beneficiaryCode = beneficiaryCode;
        this.beneficiaryName = beneficiaryName;
        this.payoutDate = payoutDate;
        this.amount = amount;
        this.approvedBy = approvedBy;
        this.remarks = remarks;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getBeneficiaryId() { return beneficiaryId; }
    public String getBeneficiaryCode() { return beneficiaryCode; }
    public String getBeneficiaryName() { return beneficiaryName; }
    public LocalDate getPayoutDate() { return payoutDate; }
    public BigDecimal getAmount() { return amount; }
    public String getApprovedBy() { return approvedBy; }
    public String getRemarks() { return remarks; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
