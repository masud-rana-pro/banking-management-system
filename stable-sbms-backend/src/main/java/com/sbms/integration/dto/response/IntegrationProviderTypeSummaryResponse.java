package com.sbms.integration.dto.response;

import com.sbms.integration.enums.IntegrationProviderType;

public record IntegrationProviderTypeSummaryResponse(
        IntegrationProviderType providerType,
        long totalProviders,
        long activeProviders
) {}
