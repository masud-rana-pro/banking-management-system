package com.sbms.shariah.service;

import com.sbms.shariah.dto.request.ShariahChecklistSaveRequest;
import com.sbms.shariah.dto.request.ShariahDecisionRequest;
import com.sbms.shariah.dto.request.ShariahReviewCaseRequest;
import com.sbms.shariah.dto.response.ShariahChecklistItemResponse;
import com.sbms.shariah.dto.response.ShariahDashboardSummaryResponse;
import com.sbms.shariah.dto.response.ShariahReviewCaseResponse;
import com.sbms.shariah.dto.response.ShariahReviewDecisionResponse;

import java.util.List;

public interface IShariahReviewService {
    ShariahReviewCaseResponse create(ShariahReviewCaseRequest request);
    List<ShariahReviewCaseResponse> list(String referenceModule, String caseStatus, String keyword);
    ShariahReviewCaseResponse getById(Long id);
    List<ShariahChecklistItemResponse> getChecklistItems();
    ShariahReviewCaseResponse saveChecklist(Long id, ShariahChecklistSaveRequest request);
    ShariahReviewCaseResponse approve(Long id, ShariahDecisionRequest request);
    ShariahReviewCaseResponse reject(Long id, ShariahDecisionRequest request);
    ShariahReviewCaseResponse returnCase(Long id, ShariahDecisionRequest request);
    List<ShariahReviewDecisionResponse> getHistory(Long id);
    ShariahDashboardSummaryResponse dashboardSummary();
}
