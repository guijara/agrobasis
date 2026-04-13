package com.agrobasis.core_service.market.infrastructure.integration;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.infrastructure.integration.dto.ExternalCommodityQuoteData;

public interface CommodityMarketQuoteClient {
    ExternalCommodityQuoteData fetchLatestCommodityQuote(Commodity commodity);
}
