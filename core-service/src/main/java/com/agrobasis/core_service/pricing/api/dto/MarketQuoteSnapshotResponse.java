package com.agrobasis.core_service.pricing.api.dto;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MarketQuoteSnapshotResponse(
        Commodity commodity,
        BigDecimal price,
        Currency currency,
        Unit unit,
        String source,
        LocalDateTime quotedAt
) {
}
