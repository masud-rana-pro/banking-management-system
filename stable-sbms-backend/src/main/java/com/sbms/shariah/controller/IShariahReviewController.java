package com.sbms.shariah.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.shariah.dto.request.ShariahChecklistSaveRequest;
import com.sbms.shariah.dto.request.ShariahDecisionRequest;
import com.sbms.shariah.dto.request.ShariahReviewCaseRequest;
import com.sbms.shariah.dto.response.ShariahChecklistItemResponse;
import com.sbms.shariah.dto.response.ShariahDashboardSummaryResponse;
import com.sbms.shariah.dto.response.ShariahReviewCaseResponse;
import com.sbms.shariah.dto.response.ShariahReviewDecisionResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IShariahReviewController {
    ApiResponse<ShariahReviewCaseResponse> create(ShariahReviewCaseRequest request);
    ApiResponse<List<ShariahReviewCaseResponse>> list(String referenceModule, String caseStatus, String keyword);
    ApiResponse<ShariahReviewCaseResponse> getById(Long id);
    ApiResponse<List<ShariahChecklistItemResponse>> getChecklistItems();
    ApiResponse<ShariahReviewCaseResponse> saveChecklist(Long id, ShariahChecklistSaveRequest request);
    ApiResponse<ShariahReviewCaseResponse> approve(Long id, ShariahDecisionRequest request, HttpServletRequest servletRequest);
    ApiResponse<ShariahReviewCaseResponse> reject(Long id, ShariahDecisionRequest request, HttpServletRequest servletRequest);
    ApiResponse<ShariahReviewCaseResponse> returnCase(Long id, ShariahDecisionRequest request, HttpServletRequest servletRequest);
    ApiResponse<List<ShariahReviewDecisionResponse>> getHistory(Long id);
    ApiResponse<ShariahDashboardSummaryResponse> dashboardSummary();
}
