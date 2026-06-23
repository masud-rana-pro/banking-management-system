package com.sbms.shariah.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.shariah.controller.IShariahReviewController;
import com.sbms.shariah.dto.request.ShariahChecklistSaveRequest;
import com.sbms.shariah.dto.request.ShariahDecisionRequest;
import com.sbms.shariah.dto.request.ShariahReviewCaseRequest;
import com.sbms.shariah.dto.response.ShariahChecklistItemResponse;
import com.sbms.shariah.dto.response.ShariahDashboardSummaryResponse;
import com.sbms.shariah.dto.response.ShariahReviewCaseResponse;
import com.sbms.shariah.dto.response.ShariahReviewDecisionResponse;
import com.sbms.shariah.service.IShariahReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shariah-reviews")
@RequiresPermission("SHARIAH_REVIEW_ACCESS")
public class ShariahReviewController implements IShariahReviewController {

    @Autowired
    private IShariahReviewService shariahReviewService;

    @Override
    @PostMapping("/create")
    public ApiResponse<ShariahReviewCaseResponse> create(@RequestBody ShariahReviewCaseRequest request) {
        return ResponseBuilder.success("Shariah review case created successfully", shariahReviewService.create(request));
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<ShariahReviewCaseResponse>> list(
            @RequestParam(required = false) String referenceModule,
            @RequestParam(required = false) String caseStatus,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Shariah review case list fetched successfully", shariahReviewService.list(referenceModule, caseStatus, keyword));
    }

    @Override
    @GetMapping("/{id}")
    public ApiResponse<ShariahReviewCaseResponse> getById(@PathVariable Long id) {
        return ResponseBuilder.success("Shariah review case fetched successfully", shariahReviewService.getById(id));
    }

    @Override
    @GetMapping("/checklist-items")
    public ApiResponse<List<ShariahChecklistItemResponse>> getChecklistItems() {
        return ResponseBuilder.success("Shariah checklist items fetched successfully", shariahReviewService.getChecklistItems());
    }

    @RequiresPermission("SHARIAH_CHECKLIST_SAVE")
    @Override
    @PostMapping("/{id}/checklist")
    public ApiResponse<ShariahReviewCaseResponse> saveChecklist(@PathVariable Long id, @RequestBody ShariahChecklistSaveRequest request) {
        return ResponseBuilder.success("Shariah checklist saved successfully", shariahReviewService.saveChecklist(id, request));
    }

    @RequiresPermission("SHARIAH_APPROVE")
    @Override
    @PostMapping("/{id}/approve")
    public ApiResponse<ShariahReviewCaseResponse> approve(@PathVariable Long id,
                                                          @RequestBody ShariahDecisionRequest request,
                                                          HttpServletRequest servletRequest) {
        ShariahReviewCaseResponse response = shariahReviewService.approve(id, request);
        return ResponseBuilder.success("Shariah review case approved successfully", response);
    }

    @RequiresPermission("SHARIAH_REJECT")
    @Override
    @PostMapping("/{id}/reject")
    public ApiResponse<ShariahReviewCaseResponse> reject(@PathVariable Long id,
                                                         @RequestBody ShariahDecisionRequest request,
                                                         HttpServletRequest servletRequest) {
        ShariahReviewCaseResponse response = shariahReviewService.reject(id, request);
        return ResponseBuilder.success("Shariah review case rejected successfully", response);
    }

    @RequiresPermission("SHARIAH_RETURN")
    @Override
    @PostMapping("/{id}/return")
    public ApiResponse<ShariahReviewCaseResponse> returnCase(@PathVariable Long id,
                                                             @RequestBody ShariahDecisionRequest request,
                                                             HttpServletRequest servletRequest) {
        ShariahReviewCaseResponse response = shariahReviewService.returnCase(id, request);
        return ResponseBuilder.success("Shariah review case returned successfully", response);
    }

    @Override
    @GetMapping("/{id}/history")
    public ApiResponse<List<ShariahReviewDecisionResponse>> getHistory(@PathVariable Long id) {
        return ResponseBuilder.success("Shariah review history fetched successfully", shariahReviewService.getHistory(id));
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<ShariahDashboardSummaryResponse> dashboardSummary() {
        return ResponseBuilder.success("Shariah dashboard summary fetched successfully", shariahReviewService.dashboardSummary());
    }
}
