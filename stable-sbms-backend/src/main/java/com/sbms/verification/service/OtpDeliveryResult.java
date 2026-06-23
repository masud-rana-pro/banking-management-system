package com.sbms.verification.service;

public record OtpDeliveryResult(
        boolean delivered,
        String providerName,
        String providerResponse
) {
}
