package com.sbms.security.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.security.dto.request.InvestigationCaseActionRequest;
import com.sbms.security.dto.response.InvestigationCaseResponse;

import java.util.List;

public interface IInvestigationController {

    ApiResponse<List<InvestigationCaseResponse>> listInvestigationCases(String caseStatus, String caseType, String keyword);

    ApiResponse<InvestigationCaseResponse> getInvestigationCaseById(Long id);

    ApiResponse<InvestigationCaseResponse> assignInvestigationCase(Long id, InvestigationCaseActionRequest request);

    ApiResponse<InvestigationCaseResponse> closeInvestigationCase(Long id, InvestigationCaseActionRequest request);
}
