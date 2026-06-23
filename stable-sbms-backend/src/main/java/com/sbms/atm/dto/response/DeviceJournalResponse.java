package com.sbms.atm.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DeviceJournalResponse {

    private Long terminalId;
    private String terminalCode;
    private String terminalName;
    private String eventType;
    private String referenceNo;
    private BigDecimal amount;
    private String status;
    private String remarks;
    private LocalDateTime eventDate;

    public DeviceJournalResponse(
            Long terminalId,
            String terminalCode,
            String terminalName,
            String eventType,
            String referenceNo,
            BigDecimal amount,
            String status,
            String remarks,
            LocalDateTime eventDate
    ) {
        this.terminalId = terminalId;
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
        this.eventType = eventType;
        this.referenceNo = referenceNo;
        this.amount = amount;
        this.status = status;
        this.remarks = remarks;
        this.eventDate = eventDate;
    }

    public Long getTerminalId() {
        return terminalId;
    }

    public String getTerminalCode() {
        return terminalCode;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }
}
