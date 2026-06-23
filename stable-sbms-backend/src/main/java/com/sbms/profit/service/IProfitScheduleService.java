package com.sbms.profit.service;

import com.sbms.profit.dto.request.ProfitScheduleRequest;
import com.sbms.profit.dto.response.ProfitScheduleResponse;

import java.util.List;

public interface IProfitScheduleService {
    ProfitScheduleResponse create(ProfitScheduleRequest request);
    List<ProfitScheduleResponse> list();
    ProfitScheduleResponse getById(Long id);
    ProfitScheduleResponse archive(Long id);
    ProfitScheduleResponse restore(Long id);
}
