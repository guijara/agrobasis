package com.agrobasis.core_service.pricing.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CurrentPricingResponse(
        Commodity commodity,
        UUID farmId,
        BigDecimal convertedPrice,
        BigDecimal costPerTon,
        BigDecimal adjustedPrice,
        BigDecimal freightPerTon,
        BigDecimal netPrice,
        BigDecimal adjustmentPerTon,
        BigDecimal commercialPrice,
        Currency targetCurrency,
        Unit unit,
        MarketQuoteSnapshotResponse marketQuote,
        ExchangeRateSnapshotResponse exchangeRate,
        CalculationMemoryResponse calculationMemory,
        LocalDateTime calculatedAt
) {
}
