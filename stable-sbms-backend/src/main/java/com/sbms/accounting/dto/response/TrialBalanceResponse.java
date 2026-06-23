package com.sbms.accounting.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TrialBalanceResponse {
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Long branchId;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private long rowCount;
    private List<TrialBalanceRowResponse> rows;

    public TrialBalanceResponse(LocalDate dateFrom, LocalDate dateTo, Long branchId, BigDecimal totalDebit,
                                BigDecimal totalCredit, long rowCount, List<TrialBalanceRowResponse> rows) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.branchId = branchId;
        this.totalDebit = totalDebit;
        this.totalCredit = totalCredit;
        this.rowCount = rowCount;
        this.rows = rows;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public Long getBranchId() {
        return branchId;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public BigDecimal getTotalCredit() {
        return totalCredit;
    }

    public long getRowCount() {
        return rowCount;
    }

    public List<TrialBalanceRowResponse> getRows() {
        return rows;
    }
}
