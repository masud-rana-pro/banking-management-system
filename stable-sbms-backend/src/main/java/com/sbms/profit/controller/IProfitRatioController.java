package com.sbms.profit.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.profit.dto.request.ProfitRatioRequest;
import com.sbms.profit.dto.response.ProfitRatioDropdownResponse;
import com.sbms.profit.dto.response.ProfitRatioResponse;

import java.util.List;

public interface IProfitRatioController {
    ApiResponse<ProfitRatioResponse> create(ProfitRatioRequest request);
    ApiResponse<List<ProfitRatioResponse>> list();
    ApiResponse<ProfitRatioResponse> getById(Long id);
    ApiResponse<ProfitRatioResponse> update(Long id, ProfitRatioRequest request);
    ApiResponse<ProfitRatioResponse> archive(Long id);
    ApiResponse<ProfitRatioResponse> restore(Long id);
    ApiResponse<List<ProfitRatioDropdownResponse>> dropdown();
}
