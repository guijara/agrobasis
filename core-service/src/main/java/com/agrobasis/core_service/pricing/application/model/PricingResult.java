package com.agrobasis.core_service.pricing.application.model;

import java.math.BigDecimal;

public record PricingResult(
        BigDecimal convertedPrice,
        BigDecimal adjustedPrice,
        BigDecimal netPrice,
        BigDecimal commercialPrice
) {
}
