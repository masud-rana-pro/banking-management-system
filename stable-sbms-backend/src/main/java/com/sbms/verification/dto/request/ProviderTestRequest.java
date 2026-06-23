package com.sbms.verification.dto.request;

public record ProviderTestRequest(
        String channelType,
        String purpose,
        String contactValue,
        String referenceModule,
        Long referenceId,
        String remarks
) {
}
