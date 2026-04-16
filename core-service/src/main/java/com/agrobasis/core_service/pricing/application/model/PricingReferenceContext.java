package com.agrobasis.core_service.pricing.application.model;

import com.agrobasis.core_service.farm.domain.Commodity;

import java.time.LocalDateTime;
import java.util.UUID;

public record PricingReferenceContext(
        UUID organizationId,
        UUID farmId,
        Commodity commodity,
        String marketQuoteSource,
        LocalDateTime marketQuoteQuotedAt,
        String exchangeRateSource,
        LocalDateTime exchangeRateQuotedAt
) {
}
