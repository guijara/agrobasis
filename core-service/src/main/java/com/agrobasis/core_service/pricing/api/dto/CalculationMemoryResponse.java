package com.agrobasis.core_service.pricing.api.dto;

import java.math.BigDecimal;

public record CalculationMemoryResponse(
        String formula,
        BigDecimal marketPrice,
        BigDecimal exchangeRate,
        BigDecimal convertedPrice
) {
}
