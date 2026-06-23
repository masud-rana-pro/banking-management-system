package com.sbms.lookup.service;

import com.sbms.lookup.dto.request.LookupTypeRequest;
import com.sbms.lookup.dto.request.LookupValueRequest;
import com.sbms.lookup.dto.response.LookupDashboardSummaryResponse;
import com.sbms.lookup.dto.response.LookupTypeResponse;
import com.sbms.lookup.dto.response.LookupValueResponse;

import java.util.List;

public interface ILookupService {
    LookupDashboardSummaryResponse getDashboardSummary();
    LookupTypeResponse createType(LookupTypeRequest request);
    List<LookupTypeResponse> listTypes();
    LookupTypeResponse getTypeById(Long id);
    LookupTypeResponse updateType(Long id, LookupTypeRequest request);
    LookupTypeResponse archiveType(Long id);
    LookupTypeResponse restoreType(Long id);
    LookupValueResponse createValue(LookupValueRequest request);
    List<LookupValueResponse> listValues(Long lookupTypeId, String keyword);
    LookupValueResponse getValueById(Long id);
    LookupValueResponse updateValue(Long id, LookupValueRequest request);
    LookupValueResponse archiveValue(Long id);
    LookupValueResponse restoreValue(Long id);
    List<LookupValueResponse> getValuesByTypeCode(String typeCode, boolean activeOnly);
}
