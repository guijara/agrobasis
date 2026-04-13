package com.agrobasis.core_service.market.application;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.api.dto.ExchangeRateResponse;
import com.agrobasis.core_service.market.api.dto.MarketQuoteResponse;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.ExchangeRate;
import com.agrobasis.core_service.market.domain.MarketQuote;
import com.agrobasis.core_service.market.domain.Unit;
import com.agrobasis.core_service.market.domain.exception.ExternalMarketIntegrationException;
import com.agrobasis.core_service.market.domain.exception.InvalidExternalMarketDataException;
import com.agrobasis.core_service.market.domain.exception.UnsupportedExternalCommodityException;
import com.agrobasis.core_service.market.domain.exception.UnsupportedExternalExchangeRatePairException;
import com.agrobasis.core_service.market.infrastructure.ExchangeRateRepository;
import com.agrobasis.core_service.market.infrastructure.MarketQuoteRepository;
import com.agrobasis.core_service.market.infrastructure.integration.BcbExchangeRateClient;
import com.agrobasis.core_service.market.infrastructure.integration.CommodityMarketQuoteClient;
import com.agrobasis.core_service.market.infrastructure.integration.dto.ExternalExchangeRateData;
import com.agrobasis.core_service.market.infrastructure.integration.dto.ExternalCommodityQuoteData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketSyncServiceTest {

    @Mock
    private CommodityMarketQuoteClient marketQuoteClient;

    @Mock
    private BcbExchangeRateClient exchangeRateClient;

    @Mock
    private MarketQuoteRepository marketQuoteRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private MarketSyncService marketSyncService;

    @Test
    @DisplayName("Should sync market quote successfully")
    void shouldSyncMarketQuoteSuccessfully() {
        LocalDateTime quotedAt = LocalDateTime.of(2026, 4, 13, 10, 0);
        ExternalCommodityQuoteData externalQuote = new ExternalCommodityQuoteData(
                Commodity.SOYBEAN,
                new BigDecimal("432.10"),
                Currency.USD,
                Unit.TON,
                "B3",
                quotedAt
        );
        when(marketQuoteClient.fetchLatestCommodityQuote(Commodity.SOYBEAN)).thenReturn(externalQuote);
        when(marketQuoteRepository.save(any(MarketQuote.class))).thenAnswer(invocation -> {
            MarketQuote marketQuote = invocation.getArgument(0);
            marketQuote.setId(UUID.randomUUID());
            return marketQuote;
        });

        MarketQuoteResponse response = marketSyncService.syncLatestMarketQuote(Commodity.SOYBEAN);

        assertThat(response.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(response.price()).isEqualByComparingTo("432.10");
        assertThat(response.currency()).isEqualTo(Currency.USD);
        assertThat(response.unit()).isEqualTo(Unit.TON);
        assertThat(response.quotedAt()).isEqualTo(quotedAt);
        assertThat(response.source()).isEqualTo("B3");
        verify(marketQuoteRepository).save(any(MarketQuote.class));
    }

    @Test
    @DisplayName("Should sync exchange rate successfully")
    void shouldSyncExchangeRateSuccessfully() {
        LocalDateTime quotedAt = LocalDateTime.of(2026, 4, 13, 10, 5);
        ExternalExchangeRateData externalRate = new ExternalExchangeRateData(
                Currency.USD,
                Currency.BRL,
                new BigDecimal("5.421300"),
                "BCB PTAX",
                quotedAt
        );
        when(exchangeRateClient.fetchLatestExchangeRate(Currency.USD, Currency.BRL)).thenReturn(externalRate);
        when(exchangeRateRepository.save(any(ExchangeRate.class))).thenAnswer(invocation -> {
            ExchangeRate exchangeRate = invocation.getArgument(0);
            exchangeRate.setId(UUID.randomUUID());
            return exchangeRate;
        });

        ExchangeRateResponse response = marketSyncService.syncLatestExchangeRate(Currency.USD, Currency.BRL);

        assertThat(response.fromCurrency()).isEqualTo(Currency.USD);
        assertThat(response.toCurrency()).isEqualTo(Currency.BRL);
        assertThat(response.rate()).isEqualByComparingTo("5.421300");
        assertThat(response.quotedAt()).isEqualTo(quotedAt);
        assertThat(response.source()).isEqualTo("BCB PTAX");
        verify(exchangeRateRepository).save(any(ExchangeRate.class));
    }

    @Test
    @DisplayName("Should fail when external market quote client fails")
    void shouldFailWhenExternalCommodityMarketQuoteClientFails() {
        when(marketQuoteClient.fetchLatestCommodityQuote(Commodity.SOYBEAN))
                .thenThrow(new ExternalMarketIntegrationException("Falha ao buscar cotação externa."));

        assertThatThrownBy(() -> marketSyncService.syncLatestMarketQuote(Commodity.SOYBEAN))
                .isInstanceOf(ExternalMarketIntegrationException.class)
                .hasMessage("Falha ao buscar cotação externa.");

        verify(marketQuoteRepository, never()).save(any(MarketQuote.class));
    }

    @Test
    @DisplayName("Should fail when external exchange rate client fails")
    void shouldFailWhenExternalBcbExchangeRateClientFails() {
        when(exchangeRateClient.fetchLatestExchangeRate(Currency.USD, Currency.BRL))
                .thenThrow(new ExternalMarketIntegrationException("Falha ao buscar câmbio externo."));

        assertThatThrownBy(() -> marketSyncService.syncLatestExchangeRate(Currency.USD, Currency.BRL))
                .isInstanceOf(ExternalMarketIntegrationException.class)
                .hasMessage("Falha ao buscar câmbio externo.");

        verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
    }

    @Test
    @DisplayName("Should fail when external market quote response is invalid")
    void shouldFailWhenExternalCommodityQuoteDataIsInvalid() {
        ExternalCommodityQuoteData externalQuote = new ExternalCommodityQuoteData(
                Commodity.SOYBEAN,
                null,
                Currency.USD,
                Unit.TON,
                "B3",
                LocalDateTime.of(2026, 4, 13, 10, 0)
        );
        when(marketQuoteClient.fetchLatestCommodityQuote(Commodity.SOYBEAN)).thenReturn(externalQuote);

        assertThatThrownBy(() -> marketSyncService.syncLatestMarketQuote(Commodity.SOYBEAN))
                .isInstanceOf(InvalidExternalMarketDataException.class)
                .hasMessage("Cotação externa de commodity inválida ou incompleta.");

        verify(marketQuoteRepository, never()).save(any(MarketQuote.class));
    }

    @Test
    @DisplayName("Should fail when external exchange rate response is invalid")
    void shouldFailWhenExternalExchangeRateDataIsInvalid() {
        ExternalExchangeRateData externalRate = new ExternalExchangeRateData(
                Currency.USD,
                Currency.BRL,
                null,
                "BCB PTAX",
                LocalDateTime.of(2026, 4, 13, 10, 5)
        );
        when(exchangeRateClient.fetchLatestExchangeRate(Currency.USD, Currency.BRL)).thenReturn(externalRate);

        assertThatThrownBy(() -> marketSyncService.syncLatestExchangeRate(Currency.USD, Currency.BRL))
                .isInstanceOf(InvalidExternalMarketDataException.class)
                .hasMessage("Taxa de câmbio externa inválida ou incompleta.");

        verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
    }

    @Test
    @DisplayName("Should fail when commodity is unsupported by external provider")
    void shouldFailWhenCommodityIsUnsupportedByExternalProvider() {
        when(marketQuoteClient.fetchLatestCommodityQuote(null))
                .thenThrow(new UnsupportedExternalCommodityException("Commodity não suportada pela integração B3 nesta fase."));

        assertThatThrownBy(() -> marketSyncService.syncLatestMarketQuote(null))
                .isInstanceOf(UnsupportedExternalCommodityException.class)
                .hasMessage("Commodity não suportada pela integração B3 nesta fase.");

        verify(marketQuoteRepository, never()).save(any(MarketQuote.class));
    }

    @Test
    @DisplayName("Should fail when exchange rate pair is unsupported by external provider")
    void shouldFailWhenExchangeRatePairIsUnsupportedByExternalProvider() {
        when(exchangeRateClient.fetchLatestExchangeRate(Currency.BRL, Currency.BRL))
                .thenThrow(new UnsupportedExternalExchangeRatePairException("Par cambial não suportado pela integração BCB nesta fase."));

        assertThatThrownBy(() -> marketSyncService.syncLatestExchangeRate(Currency.BRL, Currency.BRL))
                .isInstanceOf(UnsupportedExternalExchangeRatePairException.class)
                .hasMessage("Par cambial não suportado pela integração BCB nesta fase.");

        verify(exchangeRateRepository, never()).save(any(ExchangeRate.class));
    }
}
