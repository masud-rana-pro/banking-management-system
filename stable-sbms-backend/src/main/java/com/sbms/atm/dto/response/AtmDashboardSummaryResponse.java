package com.sbms.atm.dto.response;

import java.math.BigDecimal;

public class AtmDashboardSummaryResponse {

    private long totalTerminals;
    private long activeTerminals;
    private long lowCashAlerts;
    private long unreconciledTerminals;
    private long downtimeTerminals;
    private long todayVolumeCount;
    private BigDecimal todayVolumeAmount;

    public long getTotalTerminals() {
        return totalTerminals;
    }

    public void setTotalTerminals(long totalTerminals) {
        this.totalTerminals = totalTerminals;
    }

    public long getActiveTerminals() {
        return activeTerminals;
    }

    public void setActiveTerminals(long activeTerminals) {
        this.activeTerminals = activeTerminals;
    }

    public long getLowCashAlerts() {
        return lowCashAlerts;
    }

    public void setLowCashAlerts(long lowCashAlerts) {
        this.lowCashAlerts = lowCashAlerts;
    }

    public long getUnreconciledTerminals() {
        return unreconciledTerminals;
    }

    public void setUnreconciledTerminals(long unreconciledTerminals) {
        this.unreconciledTerminals = unreconciledTerminals;
    }

    public long getDowntimeTerminals() {
        return downtimeTerminals;
    }

    public void setDowntimeTerminals(long downtimeTerminals) {
        this.downtimeTerminals = downtimeTerminals;
    }

    public long getTodayVolumeCount() {
        return todayVolumeCount;
    }

    public void setTodayVolumeCount(long todayVolumeCount) {
        this.todayVolumeCount = todayVolumeCount;
    }

    public BigDecimal getTodayVolumeAmount() {
        return todayVolumeAmount;
    }

    public void setTodayVolumeAmount(BigDecimal todayVolumeAmount) {
        this.todayVolumeAmount = todayVolumeAmount;
    }
}
