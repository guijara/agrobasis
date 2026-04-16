package com.agrobasis.core_service.pricing.application.model;

import java.math.BigDecimal;

public record PricingScenarioRequest(
        BigDecimal marketPrice,
        BigDecimal exchangeRate,
        BigDecimal costPerTon,
        BigDecimal freightPerTon,
        BigDecimal adjustmentPerTon
) {
}
