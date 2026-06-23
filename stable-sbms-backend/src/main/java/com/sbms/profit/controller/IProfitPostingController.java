package com.sbms.profit.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.profit.dto.request.ProfitPostingRunRequest;
import com.sbms.profit.dto.response.ProfitPostingResponse;
import com.sbms.profit.dto.response.ProfitPostingRunResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IProfitPostingController {
    ApiResponse<ProfitPostingRunResponse> run(ProfitPostingRunRequest request);
    ApiResponse<List<ProfitPostingResponse>> list();
    ApiResponse<ProfitPostingResponse> getById(Long id);
    ResponseEntity<byte[]> previewAdvice(Long id);
    ResponseEntity<byte[]> downloadAdvice(Long id);
}
