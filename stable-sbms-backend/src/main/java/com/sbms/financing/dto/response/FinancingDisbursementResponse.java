package com.sbms.financing.dto.response;

import com.sbms.customer.enums.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FinancingDisbursementResponse {

    private final Long id;
    private final String disbursementNo;
    private final LocalDate disbursementDate;
    private final BigDecimal disbursedAmount;
    private final Long creditedAccountId;
    private final String creditedAccountNumber;
    private final String disbursedBy;
    private final RecordStatus status;
    private final LocalDateTime createdAt;

    public FinancingDisbursementResponse(Long id, String disbursementNo, LocalDate disbursementDate,
                                         BigDecimal disbursedAmount, Long creditedAccountId,
                                         String creditedAccountNumber, String disbursedBy,
                                         RecordStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.disbursementNo = disbursementNo;
        this.disbursementDate = disbursementDate;
        this.disbursedAmount = disbursedAmount;
        this.creditedAccountId = creditedAccountId;
        this.creditedAccountNumber = creditedAccountNumber;
        this.disbursedBy = disbursedBy;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getDisbursementNo() { return disbursementNo; }
    public LocalDate getDisbursementDate() { return disbursementDate; }
    public BigDecimal getDisbursedAmount() { return disbursedAmount; }
    public Long getCreditedAccountId() { return creditedAccountId; }
    public String getCreditedAccountNumber() { return creditedAccountNumber; }
    public String getDisbursedBy() { return disbursedBy; }
    public RecordStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
