package com.sbms.accounting.dto.response;

import java.math.BigDecimal;

public class GlJournalLineResponse {
    private Long id;
    private Integer lineNo;
    private String accountCode;
    private String entrySide;
    private BigDecimal amount;
    private String remarks;

    public GlJournalLineResponse(Long id, Integer lineNo, String accountCode, String entrySide, BigDecimal amount, String remarks) {
        this.id = id;
        this.lineNo = lineNo;
        this.accountCode = accountCode;
        this.entrySide = entrySide;
        this.amount = amount;
        this.remarks = remarks;
    }

    public Long getId() { return id; }
    public Integer getLineNo() { return lineNo; }
    public String getAccountCode() { return accountCode; }
    public String getEntrySide() { return entrySide; }
    public BigDecimal getAmount() { return amount; }
    public String getRemarks() { return remarks; }
}
