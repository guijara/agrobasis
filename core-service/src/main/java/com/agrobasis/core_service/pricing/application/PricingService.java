package com.agrobasis.core_service.pricing.application;

import com.agrobasis.core_service.cost.domain.CostProfile;
import com.agrobasis.core_service.cost.infrastructure.CostProfileRepository;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.ExchangeRate;
import com.agrobasis.core_service.market.domain.MarketQuote;
import com.agrobasis.core_service.market.domain.Unit;
import com.agrobasis.core_service.market.infrastructure.ExchangeRateRepository;
import com.agrobasis.core_service.market.infrastructure.MarketQuoteRepository;
import com.agrobasis.core_service.pricing.api.dto.CalculationMemoryResponse;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingResponse;
import com.agrobasis.core_service.pricing.api.dto.ExchangeRateSnapshotResponse;
import com.agrobasis.core_service.pricing.api.dto.MarketQuoteSnapshotResponse;
import com.agrobasis.core_service.pricing.domain.exception.CostProfileUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.ExchangeRateUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.MarketQuoteUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.UnsupportedPricingContextException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final MarketQuoteRepository marketQuoteRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final CostProfileRepository costProfileRepository;

    public CurrentPricingResponse calculateCurrentPrice(UUID organizationId, Commodity commodity) {
        MarketQuote marketQuote = marketQuoteRepository.findTopByCommodityOrderByQuotedAtDesc(commodity)
                .orElseThrow(() -> new MarketQuoteUnavailableException("Nenhuma cotação disponível para a commodity informada."));

        ExchangeRate exchangeRate = exchangeRateRepository
                .findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency.USD, Currency.BRL)
                .orElseThrow(() -> new ExchangeRateUnavailableException("Nenhuma taxa de câmbio USD para BRL disponível."));

        CostProfile costProfile = costProfileRepository.findByOrganization_IdAndCommodity(organizationId, commodity)
                .orElseThrow(() -> new CostProfileUnavailableException("Nenhum perfil de custo disponível para a organização e commodity informadas."));

        validatePricingContext(marketQuote, exchangeRate);

        BigDecimal convertedPrice = marketQuote.getPrice()
                .multiply(exchangeRate.getRate())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal adjustedPrice = convertedPrice
                .subtract(costProfile.getCostPerTon())
                .setScale(2, RoundingMode.HALF_UP);

        return new CurrentPricingResponse(
                marketQuote.getCommodity(),
                convertedPrice,
                costProfile.getCostPerTon(),
                adjustedPrice,
                Currency.BRL,
                marketQuote.getUnit(),
                new MarketQuoteSnapshotResponse(
                        marketQuote.getCommodity(),
                        marketQuote.getPrice(),
                        marketQuote.getCurrency(),
                        marketQuote.getUnit(),
                        marketQuote.getSource(),
                        marketQuote.getQuotedAt()
                ),
                new ExchangeRateSnapshotResponse(
                        exchangeRate.getFromCurrency(),
                        exchangeRate.getToCurrency(),
                        exchangeRate.getRate(),
                        exchangeRate.getSource(),
                        exchangeRate.getQuotedAt()
                ),
                new CalculationMemoryResponse(
                        "price_in_usd_per_ton × usd_brl_rate",
                        "converted_price - cost_per_ton",
                        marketQuote.getPrice(),
                        exchangeRate.getRate(),
                        costProfile.getCostPerTon(),
                        convertedPrice,
                        adjustedPrice
                ),
                LocalDateTime.now()
        );
    }

    private void validatePricingContext(MarketQuote marketQuote, ExchangeRate exchangeRate) {
        if (marketQuote.getCurrency() != Currency.USD) {
            throw new UnsupportedPricingContextException("O cálculo atual suporta apenas cotações em USD.");
        }

        if (marketQuote.getUnit() != Unit.TON) {
            throw new UnsupportedPricingContextException("O cálculo atual suporta apenas cotações por TON.");
        }

        if (exchangeRate.getFromCurrency() != Currency.USD || exchangeRate.getToCurrency() != Currency.BRL) {
            throw new UnsupportedPricingContextException("O cálculo atual suporta apenas câmbio USD para BRL.");
        }
    }
}
