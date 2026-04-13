package com.agrobasis.core_service.market.application;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.api.dto.ExchangeRateResponse;
import com.agrobasis.core_service.market.api.dto.MarketQuoteResponse;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.ExchangeRate;
import com.agrobasis.core_service.market.domain.MarketQuote;
import com.agrobasis.core_service.market.domain.exception.InvalidExternalMarketDataException;
import com.agrobasis.core_service.market.infrastructure.ExchangeRateRepository;
import com.agrobasis.core_service.market.infrastructure.MarketQuoteRepository;
import com.agrobasis.core_service.market.infrastructure.integration.BcbExchangeRateClient;
import com.agrobasis.core_service.market.infrastructure.integration.CommodityMarketQuoteClient;
import com.agrobasis.core_service.market.infrastructure.integration.dto.ExternalCommodityQuoteData;
import com.agrobasis.core_service.market.infrastructure.integration.dto.ExternalExchangeRateData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketSyncService {

    private final CommodityMarketQuoteClient commodityMarketQuoteClient;
    private final BcbExchangeRateClient bcbExchangeRateClient;
    private final MarketQuoteRepository marketQuoteRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    @Transactional
    public MarketQuoteResponse syncLatestMarketQuote(Commodity commodity) {
        log.info("Starting market quote synchronization for commodity {}", commodity);

        try {
            ExternalCommodityQuoteData externalQuote = commodityMarketQuoteClient.fetchLatestCommodityQuote(commodity);
            validateMarketQuote(externalQuote);

            MarketQuote marketQuote = new MarketQuote();
            marketQuote.setCommodity(externalQuote.commodity());
            marketQuote.setPrice(externalQuote.price());
            marketQuote.setCurrency(externalQuote.currency());
            marketQuote.setUnit(externalQuote.unit());
            marketQuote.setQuotedAt(externalQuote.quotedAt());
            marketQuote.setSource(externalQuote.source());

            MarketQuote savedMarketQuote = marketQuoteRepository.save(marketQuote);
            log.info("Finished market quote synchronization for commodity {} with quote id {}", commodity, savedMarketQuote.getId());
            return toResponse(savedMarketQuote);
        } catch (RuntimeException ex) {
            log.warn("Failed market quote synchronization for commodity {}: {}", commodity, ex.getMessage());
            throw ex;
        }
    }

    @Transactional
    public ExchangeRateResponse syncLatestExchangeRate(Currency fromCurrency, Currency toCurrency) {
        log.info("Starting exchange rate synchronization for pair {}/{}", fromCurrency, toCurrency);

        try {
            ExternalExchangeRateData externalRate = bcbExchangeRateClient.fetchLatestExchangeRate(fromCurrency, toCurrency);
            validateExchangeRate(externalRate);

            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setFromCurrency(externalRate.fromCurrency());
            exchangeRate.setToCurrency(externalRate.toCurrency());
            exchangeRate.setRate(externalRate.rate());
            exchangeRate.setQuotedAt(externalRate.quotedAt());
            exchangeRate.setSource(externalRate.source());

            ExchangeRate savedExchangeRate = exchangeRateRepository.save(exchangeRate);
            log.info("Finished exchange rate synchronization for pair {}/{} with rate id {}", fromCurrency, toCurrency, savedExchangeRate.getId());
            return toResponse(savedExchangeRate);
        } catch (RuntimeException ex) {
            log.warn("Failed exchange rate synchronization for pair {}/{}: {}", fromCurrency, toCurrency, ex.getMessage());
            throw ex;
        }
    }

    private void validateMarketQuote(ExternalCommodityQuoteData externalQuote) {
        if (externalQuote == null
                || externalQuote.commodity() == null
                || externalQuote.price() == null
                || externalQuote.price().compareTo(BigDecimal.ZERO) <= 0
                || externalQuote.currency() == null
                || externalQuote.unit() == null
                || externalQuote.quotedAt() == null
                || externalQuote.source() == null
                || externalQuote.source().isBlank()) {
            throw new InvalidExternalMarketDataException("Cotação externa de commodity inválida ou incompleta.");
        }
    }

    private void validateExchangeRate(ExternalExchangeRateData externalRate) {
        if (externalRate == null
                || externalRate.fromCurrency() == null
                || externalRate.toCurrency() == null
                || externalRate.rate() == null
                || externalRate.rate().compareTo(BigDecimal.ZERO) <= 0
                || externalRate.quotedAt() == null
                || externalRate.source() == null
                || externalRate.source().isBlank()) {
            throw new InvalidExternalMarketDataException("Taxa de câmbio externa inválida ou incompleta.");
        }
    }

    private MarketQuoteResponse toResponse(MarketQuote marketQuote) {
        return new MarketQuoteResponse(
                marketQuote.getId(),
                marketQuote.getCommodity(),
                marketQuote.getSource(),
                marketQuote.getPrice(),
                marketQuote.getCurrency(),
                marketQuote.getUnit(),
                marketQuote.getQuotedAt(),
                marketQuote.getCreatedAt()
        );
    }

    private ExchangeRateResponse toResponse(ExchangeRate exchangeRate) {
        return new ExchangeRateResponse(
                exchangeRate.getId(),
                exchangeRate.getFromCurrency(),
                exchangeRate.getToCurrency(),
                exchangeRate.getRate(),
                exchangeRate.getSource(),
                exchangeRate.getQuotedAt(),
                exchangeRate.getCreatedAt()
        );
    }
}
