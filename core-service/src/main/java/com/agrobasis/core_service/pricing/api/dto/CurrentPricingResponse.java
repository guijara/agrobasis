package com.agrobasis.core_service.pricing.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CurrentPricingResponse(
        Commodity commodity,
        BigDecimal convertedPrice,
        Currency targetCurrency,
        Unit unit,
        MarketQuoteSnapshotResponse marketQuote,
        ExchangeRateSnapshotResponse exchangeRate,
        CalculationMemoryResponse calculationMemory,
        LocalDateTime calculatedAt
) {
}
