package com.agrobasis.core_service.pricing.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CurrentPricingAnalysisResponse(
        Commodity commodity,
        UUID farmId,
        BigDecimal convertedPrice,
        BigDecimal adjustedPrice,
        BigDecimal netPrice,
        BigDecimal commercialPrice,
        PricingCompositionResponse composition,
        PricingImpactSummaryResponse impactSummary,
        List<PricingIndicatorResponse> indicators,
        LocalDateTime calculatedAt
) {
}
