package com.sbms.zakat.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ZakatDashboardSummaryResponse {
    private final Long zakatDueAccounts;
    private final BigDecimal totalZakatCalculated;
    private final BigDecimal charityFundBalance;
    private final BigDecimal beneficiaryPayoutTotal;
    private final Long upcomingZakatReminders;
    private final List<ZakatProfileResponse> recentProfiles;
    private final List<CharityFundResponse> recentFundMovements;
    private final List<CharityPayoutResponse> recentPayouts;

    public ZakatDashboardSummaryResponse(Long zakatDueAccounts, BigDecimal totalZakatCalculated,
                                         BigDecimal charityFundBalance, BigDecimal beneficiaryPayoutTotal,
                                         Long upcomingZakatReminders, List<ZakatProfileResponse> recentProfiles,
                                         List<CharityFundResponse> recentFundMovements,
                                         List<CharityPayoutResponse> recentPayouts) {
        this.zakatDueAccounts = zakatDueAccounts;
        this.totalZakatCalculated = totalZakatCalculated;
        this.charityFundBalance = charityFundBalance;
        this.beneficiaryPayoutTotal = beneficiaryPayoutTotal;
        this.upcomingZakatReminders = upcomingZakatReminders;
        this.recentProfiles = recentProfiles;
        this.recentFundMovements = recentFundMovements;
        this.recentPayouts = recentPayouts;
    }

    public Long getZakatDueAccounts() { return zakatDueAccounts; }
    public BigDecimal getTotalZakatCalculated() { return totalZakatCalculated; }
    public BigDecimal getCharityFundBalance() { return charityFundBalance; }
    public BigDecimal getBeneficiaryPayoutTotal() { return beneficiaryPayoutTotal; }
    public Long getUpcomingZakatReminders() { return upcomingZakatReminders; }
    public List<ZakatProfileResponse> getRecentProfiles() { return recentProfiles; }
    public List<CharityFundResponse> getRecentFundMovements() { return recentFundMovements; }
    public List<CharityPayoutResponse> getRecentPayouts() { return recentPayouts; }
}
