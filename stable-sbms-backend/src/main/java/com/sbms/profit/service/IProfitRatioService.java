package com.sbms.profit.service;

import com.sbms.profit.dto.request.ProfitRatioRequest;
import com.sbms.profit.dto.response.ProfitRatioDropdownResponse;
import com.sbms.profit.dto.response.ProfitRatioResponse;

import java.util.List;

public interface IProfitRatioService {
    ProfitRatioResponse create(ProfitRatioRequest request);
    List<ProfitRatioResponse> list();
    ProfitRatioResponse getById(Long id);
    ProfitRatioResponse update(Long id, ProfitRatioRequest request);
    ProfitRatioResponse archive(Long id);
    ProfitRatioResponse restore(Long id);
    List<ProfitRatioDropdownResponse> dropdown();
}
