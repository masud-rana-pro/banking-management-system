package com.sbms.profit.dto.response;

import java.util.List;

public record ProfitPostingRunResponse(
        int processedCount,
        int postedCount,
        int failedCount,
        List<ProfitPostingResponse> postings
) {
}
