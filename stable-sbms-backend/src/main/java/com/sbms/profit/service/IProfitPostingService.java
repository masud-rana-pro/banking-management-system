package com.sbms.profit.service;

import com.sbms.profit.dto.request.ProfitPostingRunRequest;
import com.sbms.profit.dto.response.ProfitDashboardSummaryResponse;
import com.sbms.profit.dto.response.ProfitPostingResponse;
import com.sbms.profit.dto.response.ProfitPostingRunResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IProfitPostingService {
    ProfitPostingRunResponse run(ProfitPostingRunRequest request, String username);
    List<ProfitPostingResponse> list();
    ProfitPostingResponse getById(Long id);
    ResponseEntity<byte[]> previewAdvice(Long id);
    ResponseEntity<byte[]> downloadAdvice(Long id);
    ProfitDashboardSummaryResponse dashboardSummary();
}
