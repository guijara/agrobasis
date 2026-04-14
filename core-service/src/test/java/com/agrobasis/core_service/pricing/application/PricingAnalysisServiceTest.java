package com.agrobasis.core_service.pricing.application;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;
import com.agrobasis.core_service.pricing.api.dto.CalculationMemoryResponse;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingAnalysisResponse;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingResponse;
import com.agrobasis.core_service.pricing.api.dto.ExchangeRateSnapshotResponse;
import com.agrobasis.core_service.pricing.api.dto.MarketQuoteSnapshotResponse;
import com.agrobasis.core_service.pricing.api.dto.PricingIndicatorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingAnalysisServiceTest {

    private static final UUID ORGANIZATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FARM_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final LocalDateTime CALCULATED_AT = LocalDateTime.of(2026, 4, 14, 9, 30);

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private PricingAnalysisService pricingAnalysisService;

    @Test
    @DisplayName("Should analyze current pricing and calculate indicators")
    void shouldAnalyzeCurrentPricingAndCalculateIndicators() {
        when(pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .thenReturn(currentPricingResponse());

        CurrentPricingAnalysisResponse result = pricingAnalysisService.analyzeCurrentPricing(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN);

        assertThat(result.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(result.farmId()).isEqualTo(FARM_ID);
        assertThat(result.convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(result.adjustedPrice()).isEqualByComparingTo("673.05");
        assertThat(result.netPrice()).isEqualByComparingTo("653.05");
        assertThat(result.commercialPrice()).isEqualByComparingTo("643.05");
        assertThat(result.calculatedAt()).isEqualTo(CALCULATED_AT);

        assertThat(result.composition().marketPriceInSourceCurrency()).isEqualByComparingTo("132.45");
        assertThat(result.composition().exchangeRate()).isEqualByComparingTo("5.421300");
        assertThat(result.composition().convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(result.composition().costPerTon()).isEqualByComparingTo("45.00");
        assertThat(result.composition().freightPerTon()).isEqualByComparingTo("20.00");
        assertThat(result.composition().adjustmentPerTon()).isEqualByComparingTo("10.00");
        assertThat(result.composition().commercialPrice()).isEqualByComparingTo("643.05");

        assertThat(result.impactSummary().totalReductionFromCostsAndAdjustments()).isEqualByComparingTo("75.00");
        assertThat(result.impactSummary().marketToCommercialDelta()).isEqualByComparingTo("75.00");
        assertThat(result.impactSummary().costImpact()).isEqualByComparingTo("45.00");
        assertThat(result.impactSummary().freightImpact()).isEqualByComparingTo("20.00");
        assertThat(result.impactSummary().commercialAdjustmentImpact()).isEqualByComparingTo("10.00");

        assertThat(result.indicators()).hasSize(5);
        Map<String, PricingIndicatorResponse> indicators = result.indicators()
                .stream()
                .collect(Collectors.toMap(PricingIndicatorResponse::name, indicator -> indicator));

        assertIndicator(indicators, "cost_share_of_converted_price", "6.27");
        assertIndicator(indicators, "freight_share_of_converted_price", "2.79");
        assertIndicator(indicators, "commercial_adjustment_share_of_converted_price", "1.39");
        assertIndicator(indicators, "commercial_price_retention", "89.56");
        assertIndicator(indicators, "total_reduction_share_of_converted_price", "10.44");
        verify(pricingService).calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN);
    }

    private void assertIndicator(Map<String, PricingIndicatorResponse> indicators, String name, String expectedValue) {
        assertThat(indicators).containsKey(name);
        assertThat(indicators.get(name).value()).isEqualByComparingTo(expectedValue);
        assertThat(indicators.get(name).unit()).isEqualTo("PERCENT");
    }

    private CurrentPricingResponse currentPricingResponse() {
        return new CurrentPricingResponse(
                Commodity.SOYBEAN,
                FARM_ID,
                new BigDecimal("718.05"),
                new BigDecimal("45.00"),
                new BigDecimal("673.05"),
                new BigDecimal("20.00"),
                new BigDecimal("653.05"),
                new BigDecimal("10.00"),
                new BigDecimal("643.05"),
                Currency.BRL,
                Unit.TON,
                new MarketQuoteSnapshotResponse(
                        Commodity.SOYBEAN,
                        new BigDecimal("132.45"),
                        Currency.USD,
                        Unit.TON,
                        "CEPEA",
                        LocalDateTime.of(2026, 4, 7, 10, 0)
                ),
                new ExchangeRateSnapshotResponse(
                        Currency.USD,
                        Currency.BRL,
                        new BigDecimal("5.421300"),
                        "Banco Central",
                        LocalDateTime.of(2026, 4, 7, 10, 5)
                ),
                new CalculationMemoryResponse(
                        "price_in_usd_per_ton × usd_brl_rate",
                        "converted_price - cost_per_ton",
                        "adjusted_price - freight_per_ton",
                        "net_price - adjustment_per_ton",
                        new BigDecimal("132.45"),
                        new BigDecimal("5.421300"),
                        new BigDecimal("45.00"),
                        new BigDecimal("20.00"),
                        new BigDecimal("10.00"),
                        new BigDecimal("718.05"),
                        new BigDecimal("673.05"),
                        new BigDecimal("653.05"),
                        new BigDecimal("643.05")
                ),
                CALCULATED_AT
        );
    }
}
