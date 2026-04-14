package com.agrobasis.core_service.pricing.api.dto;

import java.math.BigDecimal;

public record PricingImpactSummaryResponse(
        BigDecimal totalReductionFromCostsAndAdjustments,
        BigDecimal marketToCommercialDelta,
        BigDecimal costImpact,
        BigDecimal freightImpact,
        BigDecimal commercialAdjustmentImpact
) {
}
