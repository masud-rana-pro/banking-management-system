package com.sbms.statement.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.statement.controller.IStatementDashboardController;
import com.sbms.statement.dto.response.FileReferenceResponse;
import com.sbms.statement.dto.response.StatementDashboardSummaryResponse;
import com.sbms.statement.service.IStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statements")
public class StatementDashboardController implements IStatementDashboardController {

    @Autowired
    private IStatementService statementService;

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<StatementDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Statement dashboard summary fetched successfully", statementService.dashboardSummary());
    }

    @Override
    @GetMapping("/files")
    public ApiResponse<List<FileReferenceResponse>> files() {
        return ResponseBuilder.success("Statement export center fetched successfully", statementService.listFiles());
    }
}
