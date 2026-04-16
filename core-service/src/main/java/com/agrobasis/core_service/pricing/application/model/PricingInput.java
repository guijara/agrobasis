package com.agrobasis.core_service.pricing.application.model;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;

import java.math.BigDecimal;
import java.util.UUID;

public record PricingInput(
        Commodity commodity,
        UUID farmId,
        BigDecimal marketPrice,
        Currency marketCurrency,
        Unit unit,
        BigDecimal exchangeRate,
        BigDecimal costPerTon,
        BigDecimal freightPerTon,
        BigDecimal adjustmentPerTon
) {
}
