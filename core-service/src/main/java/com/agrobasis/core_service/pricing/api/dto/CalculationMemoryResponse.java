package com.agrobasis.core_service.pricing.api.dto;

import java.math.BigDecimal;

public record CalculationMemoryResponse(
        String conversionFormula,
        String adjustmentFormula,
        String freightFormula,
        BigDecimal marketPrice,
        BigDecimal exchangeRate,
        BigDecimal costPerTon,
        BigDecimal freightPerTon,
        BigDecimal convertedPrice,
        BigDecimal adjustedPrice,
        BigDecimal netPrice
) {
}
