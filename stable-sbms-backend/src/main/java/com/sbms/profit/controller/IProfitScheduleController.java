package com.sbms.profit.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.profit.dto.request.ProfitScheduleRequest;
import com.sbms.profit.dto.response.ProfitScheduleResponse;

import java.util.List;

public interface IProfitScheduleController {
    ApiResponse<ProfitScheduleResponse> create(ProfitScheduleRequest request);
    ApiResponse<List<ProfitScheduleResponse>> list();
    ApiResponse<ProfitScheduleResponse> getById(Long id);
    ApiResponse<ProfitScheduleResponse> archive(Long id);
    ApiResponse<ProfitScheduleResponse> restore(Long id);
}
