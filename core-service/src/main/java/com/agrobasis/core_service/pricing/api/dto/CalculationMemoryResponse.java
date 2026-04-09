package com.agrobasis.core_service.pricing.api.dto;

import java.math.BigDecimal;

public record CalculationMemoryResponse(
        String conversionFormula,
        String adjustmentFormula,
        BigDecimal marketPrice,
        BigDecimal exchangeRate,
        BigDecimal costPerTon,
        BigDecimal convertedPrice,
        BigDecimal adjustedPrice
) {
}
