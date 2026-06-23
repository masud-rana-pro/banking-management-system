package com.sbms.dashboard.service;

import com.sbms.dashboard.dto.GeneralDashboardResponse;

import java.time.LocalDate;

public interface IGeneralDashboardService {
    GeneralDashboardResponse getOverview(Long branchId, LocalDate dateFrom, LocalDate dateTo, String window);
}

