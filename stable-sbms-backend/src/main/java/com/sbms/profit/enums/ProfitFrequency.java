package com.sbms.profit.enums;

import java.time.LocalDate;

public enum ProfitFrequency {
    MONTHLY(1),
    QUARTERLY(3),
    HALF_YEARLY(6),
    YEARLY(12);

    private final int monthSpan;

    ProfitFrequency(int monthSpan) {
        this.monthSpan = monthSpan;
    }

    public int getMonthSpan() {
        return monthSpan;
    }

    public LocalDate nextDate(LocalDate currentDate) {
        return currentDate == null ? null : currentDate.plusMonths(monthSpan);
    }
}
