package com.sbms.accounting.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "gl_journal_line")
public class GlJournalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "journal_id", nullable = false)
    private Long journalId;

    @Column(name = "line_no", nullable = false)
    private Integer lineNo;

    @Column(name = "account_code", nullable = false, length = 30)
    private String accountCode;

    @Column(name = "entry_side", nullable = false, length = 10)
    private String entrySide;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @PrePersist
    public void prePersist() {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJournalId() { return journalId; }
    public void setJournalId(Long journalId) { this.journalId = journalId; }
    public Integer getLineNo() { return lineNo; }
    public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }
    public String getEntrySide() { return entrySide; }
    public void setEntrySide(String entrySide) { this.entrySide = entrySide; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
