package com.sbms.statement.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.statement.dto.response.FileReferenceResponse;
import com.sbms.statement.dto.response.StatementDashboardSummaryResponse;

import java.util.List;

public interface IStatementDashboardController {
    ApiResponse<StatementDashboardSummaryResponse> dashboardSummary();
    ApiResponse<List<FileReferenceResponse>> files();
}
