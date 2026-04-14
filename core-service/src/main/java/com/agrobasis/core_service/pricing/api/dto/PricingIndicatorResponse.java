package com.agrobasis.core_service.pricing.api.dto;

import java.math.BigDecimal;

public record PricingIndicatorResponse(
        String name,
        BigDecimal value,
        String unit
) {
}
