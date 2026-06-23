package com.sbms.customer.dto.response;

public class CustomerDashboardSummaryResponse {

    private Long totalCustomers;
    private Long activeCustomers;
    private Long pendingKycCustomers;
    private Long blockedCustomers;
    private Long newCustomersThisMonth;
    private Long incompleteProfiles;

    public CustomerDashboardSummaryResponse() {
    }

    public CustomerDashboardSummaryResponse(Long totalCustomers, Long activeCustomers, Long pendingKycCustomers,
                                            Long blockedCustomers, Long newCustomersThisMonth,
                                            Long incompleteProfiles) {
        this.totalCustomers = totalCustomers;
        this.activeCustomers = activeCustomers;
        this.pendingKycCustomers = pendingKycCustomers;
        this.blockedCustomers = blockedCustomers;
        this.newCustomersThisMonth = newCustomersThisMonth;
        this.incompleteProfiles = incompleteProfiles;
    }

    public Long getTotalCustomers() {
        return totalCustomers;
    }

    public Long getActiveCustomers() {
        return activeCustomers;
    }

    public Long getPendingKycCustomers() {
        return pendingKycCustomers;
    }

    public Long getBlockedCustomers() {
        return blockedCustomers;
    }

    public Long getNewCustomersThisMonth() {
        return newCustomersThisMonth;
    }

    public Long getIncompleteProfiles() {
        return incompleteProfiles;
    }
}