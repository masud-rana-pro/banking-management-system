package com.sbms.lookup.controller;

import com.sbms.common.response.ApiResponse;
import com.sbms.lookup.dto.request.LookupTypeRequest;
import com.sbms.lookup.dto.request.LookupValueRequest;
import com.sbms.lookup.dto.response.LookupDashboardSummaryResponse;
import com.sbms.lookup.dto.response.LookupTypeResponse;
import com.sbms.lookup.dto.response.LookupValueResponse;

import java.util.List;

public interface ILookupController {
    ApiResponse<LookupDashboardSummaryResponse> getDashboardSummary();
    ApiResponse<LookupTypeResponse> createType(LookupTypeRequest request);
    ApiResponse<List<LookupTypeResponse>> listTypes();
    ApiResponse<LookupTypeResponse> getTypeById(Long id);
    ApiResponse<LookupTypeResponse> updateType(Long id, LookupTypeRequest request);
    ApiResponse<LookupTypeResponse> archiveType(Long id);
    ApiResponse<LookupTypeResponse> restoreType(Long id);
    ApiResponse<LookupValueResponse> createValue(LookupValueRequest request);
    ApiResponse<List<LookupValueResponse>> listValues(Long lookupTypeId, String keyword);
    ApiResponse<LookupValueResponse> getValueById(Long id);
    ApiResponse<LookupValueResponse> updateValue(Long id, LookupValueRequest request);
    ApiResponse<LookupValueResponse> archiveValue(Long id);
    ApiResponse<LookupValueResponse> restoreValue(Long id);
    ApiResponse<List<LookupValueResponse>> getValuesByTypeCode(String typeCode);
    ApiResponse<List<LookupValueResponse>> getDropdownValues(String typeCode);
}
