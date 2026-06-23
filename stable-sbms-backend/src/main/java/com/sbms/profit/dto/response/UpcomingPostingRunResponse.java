package com.sbms.profit.dto.response;

import java.time.LocalDate;

public record UpcomingPostingRunResponse(
        LocalDate nextPostingDate,
        Long pendingSchedules
) {
}
