package com.sbms.lookup.dto.response;

import java.util.List;

public record LookupDashboardSummaryResponse(
        long lookupTypeCount,
        long lookupValueCount,
        long activeValueCount,
        long recentlyChangedCount,
        List<LookupTypeResponse> recentTypes,
        List<LookupValueResponse> recentValues
) {}
