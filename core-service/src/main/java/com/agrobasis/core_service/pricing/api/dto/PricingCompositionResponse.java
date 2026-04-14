package com.agrobasis.core_service.pricing.api.dto;

import java.math.BigDecimal;

public record PricingCompositionResponse(
        BigDecimal marketPriceInSourceCurrency,
        BigDecimal exchangeRate,
        BigDecimal convertedPrice,
        BigDecimal costPerTon,
        BigDecimal freightPerTon,
        BigDecimal adjustmentPerTon,
        BigDecimal commercialPrice
) {
}
