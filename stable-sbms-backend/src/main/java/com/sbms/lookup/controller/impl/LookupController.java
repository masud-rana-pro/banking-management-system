package com.sbms.lookup.controller.impl;

import com.sbms.common.response.ApiResponse;
import com.sbms.common.response.ResponseBuilder;
import com.sbms.config.RequiresPermission;
import com.sbms.lookup.controller.ILookupController;
import com.sbms.lookup.dto.request.LookupTypeRequest;
import com.sbms.lookup.dto.request.LookupValueRequest;
import com.sbms.lookup.dto.response.LookupDashboardSummaryResponse;
import com.sbms.lookup.dto.response.LookupTypeResponse;
import com.sbms.lookup.dto.response.LookupValueResponse;
import com.sbms.lookup.service.ILookupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lookups")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
@RequiresPermission("LOOKUP_CONFIG_ACCESS")
public class LookupController implements ILookupController {

    private final ILookupService lookupService;

    public LookupController(ILookupService lookupService) {
        this.lookupService = lookupService;
    }

    @Override
    @GetMapping("/dashboard-summary")
    public ApiResponse<LookupDashboardSummaryResponse> getDashboardSummary() {
        return ResponseBuilder.success("Lookup dashboard summary fetched successfully", lookupService.getDashboardSummary());
    }

    @Override
    @RequiresPermission("LOOKUP_TYPE_CREATE")
    @PostMapping("/types/create")
    public ApiResponse<LookupTypeResponse> createType(@RequestBody LookupTypeRequest request) {
        return ResponseBuilder.success("Lookup type created successfully", lookupService.createType(request));
    }

    @Override
    @GetMapping("/types/list")
    public ApiResponse<List<LookupTypeResponse>> listTypes() {
        return ResponseBuilder.success("Lookup types fetched successfully", lookupService.listTypes());
    }

    @Override
    @GetMapping("/types/{id}")
    public ApiResponse<LookupTypeResponse> getTypeById(@PathVariable Long id) {
        return ResponseBuilder.success("Lookup type detail fetched successfully", lookupService.getTypeById(id));
    }

    @Override
    @RequiresPermission("LOOKUP_TYPE_EDIT")
    @PutMapping("/types/{id}")
    public ApiResponse<LookupTypeResponse> updateType(@PathVariable Long id, @RequestBody LookupTypeRequest request) {
        return ResponseBuilder.success("Lookup type updated successfully", lookupService.updateType(id, request));
    }

    @Override
    @RequiresPermission("LOOKUP_TYPE_ARCHIVE")
    @DeleteMapping("/types/{id}")
    public ApiResponse<LookupTypeResponse> archiveType(@PathVariable Long id) {
        return ResponseBuilder.success("Lookup type archived successfully", lookupService.archiveType(id));
    }

    @Override
    @RequiresPermission("LOOKUP_TYPE_RESTORE")
    @PutMapping("/types/{id}/restore")
    public ApiResponse<LookupTypeResponse> restoreType(@PathVariable Long id) {
        return ResponseBuilder.success("Lookup type restored successfully", lookupService.restoreType(id));
    }

    @Override
    @RequiresPermission("LOOKUP_VALUE_CREATE")
    @PostMapping("/values/create")
    public ApiResponse<LookupValueResponse> createValue(@RequestBody LookupValueRequest request) {
        return ResponseBuilder.success("Lookup value created successfully", lookupService.createValue(request));
    }

    @Override
    @GetMapping("/values/list")
    public ApiResponse<List<LookupValueResponse>> listValues(
            @RequestParam(required = false) Long lookupTypeId,
            @RequestParam(required = false) String keyword
    ) {
        return ResponseBuilder.success("Lookup values fetched successfully", lookupService.listValues(lookupTypeId, keyword));
    }

    @Override
    @GetMapping("/values/{id}")
    public ApiResponse<LookupValueResponse> getValueById(@PathVariable Long id) {
        return ResponseBuilder.success("Lookup value detail fetched successfully", lookupService.getValueById(id));
    }

    @Override
    @RequiresPermission("LOOKUP_VALUE_EDIT")
    @PutMapping("/values/{id}")
    public ApiResponse<LookupValueResponse> updateValue(@PathVariable Long id, @RequestBody LookupValueRequest request) {
        return ResponseBuilder.success("Lookup value updated successfully", lookupService.updateValue(id, request));
    }

    @Override
    @RequiresPermission("LOOKUP_VALUE_ARCHIVE")
    @DeleteMapping("/values/{id}")
    public ApiResponse<LookupValueResponse> archiveValue(@PathVariable Long id) {
        return ResponseBuilder.success("Lookup value archived successfully", lookupService.archiveValue(id));
    }

    @Override
    @RequiresPermission("LOOKUP_VALUE_RESTORE")
    @PutMapping("/values/{id}/restore")
    public ApiResponse<LookupValueResponse> restoreValue(@PathVariable Long id) {
        return ResponseBuilder.success("Lookup value restored successfully", lookupService.restoreValue(id));
    }

    @Override
    @GetMapping("/values/by-type/{typeCode}")
    public ApiResponse<List<LookupValueResponse>> getValuesByTypeCode(@PathVariable String typeCode) {
        return ResponseBuilder.success("Lookup values fetched successfully", lookupService.getValuesByTypeCode(typeCode, false));
    }

    @Override
    @GetMapping("/dropdown/{typeCode}")
    public ApiResponse<List<LookupValueResponse>> getDropdownValues(@PathVariable String typeCode) {
        return ResponseBuilder.success("Lookup dropdown values fetched successfully", lookupService.getValuesByTypeCode(typeCode, true));
    }
}
