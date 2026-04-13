package com.agrobasis.core_service.pricing.application;

import com.agrobasis.core_service.cost.domain.CommercialAdjustmentProfile;
import com.agrobasis.core_service.cost.domain.CostProfile;
import com.agrobasis.core_service.cost.domain.FreightProfile;
import com.agrobasis.core_service.cost.infrastructure.CommercialAdjustmentProfileRepository;
import com.agrobasis.core_service.cost.infrastructure.CostProfileRepository;
import com.agrobasis.core_service.cost.infrastructure.FreightProfileRepository;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.farm.domain.Farm;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.ExchangeRate;
import com.agrobasis.core_service.market.domain.MarketQuote;
import com.agrobasis.core_service.market.domain.Unit;
import com.agrobasis.core_service.market.infrastructure.ExchangeRateRepository;
import com.agrobasis.core_service.market.infrastructure.MarketQuoteRepository;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingResponse;
import com.agrobasis.core_service.pricing.domain.exception.CommercialAdjustmentProfileUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.CostProfileUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.ExchangeRateUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.FreightProfileUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.MarketQuoteUnavailableException;
import com.agrobasis.core_service.pricing.domain.exception.UnsupportedPricingContextException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingServiceTest {

    private static final UUID ORGANIZATION_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FARM_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Mock
    private MarketQuoteRepository marketQuoteRepository;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @Mock
    private CostProfileRepository costProfileRepository;

    @Mock
    private FreightProfileRepository freightProfileRepository;

    @Mock
    private CommercialAdjustmentProfileRepository commercialAdjustmentProfileRepository;

    @InjectMocks
    private PricingService pricingService;

    @Test
    @DisplayName("Should calculate current price successfully with commercial adjustment")
    void shouldCalculateCurrentPriceSuccessfullyWithCommercialAdjustment() {
        MarketQuote marketQuote = createMarketQuote(Commodity.SOYBEAN, "132.45", Currency.USD, Unit.TON, "CEPEA");
        ExchangeRate exchangeRate = createExchangeRate(Currency.USD, Currency.BRL, "5.421300", "Banco Central");
        CostProfile costProfile = createCostProfile(Commodity.SOYBEAN, "45.00");
        FreightProfile freightProfile = createFreightProfile(Commodity.SOYBEAN, "20.00");
        CommercialAdjustmentProfile commercialAdjustmentProfile = createCommercialAdjustmentProfile(Commodity.SOYBEAN, "10.00");

        stubPricingDependencies(marketQuote, exchangeRate, costProfile, freightProfile, commercialAdjustmentProfile);

        CurrentPricingResponse result = pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN);

        assertThat(result.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(result.farmId()).isEqualTo(FARM_ID);
        assertThat(result.targetCurrency()).isEqualTo(Currency.BRL);
        assertThat(result.unit()).isEqualTo(Unit.TON);
        assertThat(result.convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(result.costPerTon()).isEqualByComparingTo("45.00");
        assertThat(result.adjustedPrice()).isEqualByComparingTo("673.05");
        assertThat(result.freightPerTon()).isEqualByComparingTo("20.00");
        assertThat(result.netPrice()).isEqualByComparingTo("653.05");
        assertThat(result.adjustmentPerTon()).isEqualByComparingTo("10.00");
        assertThat(result.commercialPrice()).isEqualByComparingTo("643.05");
        assertThat(result.marketQuote().source()).isEqualTo("CEPEA");
        assertThat(result.exchangeRate().source()).isEqualTo("Banco Central");
        assertThat(result.calculationMemory().conversionFormula()).isEqualTo("price_in_usd_per_ton × usd_brl_rate");
        assertThat(result.calculationMemory().adjustmentFormula()).isEqualTo("converted_price - cost_per_ton");
        assertThat(result.calculationMemory().freightFormula()).isEqualTo("adjusted_price - freight_per_ton");
        assertThat(result.calculationMemory().commercialFormula()).isEqualTo("net_price - adjustment_per_ton");
        assertThat(result.calculationMemory().costPerTon()).isEqualByComparingTo("45.00");
        assertThat(result.calculationMemory().freightPerTon()).isEqualByComparingTo("20.00");
        assertThat(result.calculationMemory().adjustmentPerTon()).isEqualByComparingTo("10.00");
        assertThat(result.calculationMemory().convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(result.calculationMemory().adjustedPrice()).isEqualByComparingTo("673.05");
        assertThat(result.calculationMemory().netPrice()).isEqualByComparingTo("653.05");
        assertThat(result.calculationMemory().commercialPrice()).isEqualByComparingTo("643.05");
        verify(marketQuoteRepository).findTopByCommodityOrderByQuotedAtDesc(Commodity.SOYBEAN);
        verify(exchangeRateRepository).findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency.USD, Currency.BRL);
        verify(costProfileRepository).findByOrganization_IdAndCommodity(ORGANIZATION_ID, Commodity.SOYBEAN);
        verify(freightProfileRepository).findByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN);
        verify(commercialAdjustmentProfileRepository).findByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN);
    }

    @Test
    @DisplayName("Should throw exception when market quote is unavailable")
    void shouldThrowExceptionWhenMarketQuoteIsUnavailable() {
        when(marketQuoteRepository.findTopByCommodityOrderByQuotedAtDesc(Commodity.SOYBEAN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .isInstanceOf(MarketQuoteUnavailableException.class);
    }

    @Test
    @DisplayName("Should throw exception when exchange rate is unavailable")
    void shouldThrowExceptionWhenExchangeRateIsUnavailable() {
        MarketQuote marketQuote = createMarketQuote(Commodity.SOYBEAN, "132.45", Currency.USD, Unit.TON, "CEPEA");

        when(marketQuoteRepository.findTopByCommodityOrderByQuotedAtDesc(Commodity.SOYBEAN))
                .thenReturn(Optional.of(marketQuote));
        when(exchangeRateRepository.findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency.USD, Currency.BRL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .isInstanceOf(ExchangeRateUnavailableException.class);
    }

    @Test
    @DisplayName("Should throw exception when cost profile is unavailable")
    void shouldThrowExceptionWhenCostProfileIsUnavailable() {
        MarketQuote marketQuote = createMarketQuote(Commodity.SOYBEAN, "132.45", Currency.USD, Unit.TON, "CEPEA");
        ExchangeRate exchangeRate = createExchangeRate(Currency.USD, Currency.BRL, "5.421300", "Banco Central");

        when(marketQuoteRepository.findTopByCommodityOrderByQuotedAtDesc(Commodity.SOYBEAN))
                .thenReturn(Optional.of(marketQuote));
        when(exchangeRateRepository.findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency.USD, Currency.BRL))
                .thenReturn(Optional.of(exchangeRate));
        when(costProfileRepository.findByOrganization_IdAndCommodity(ORGANIZATION_ID, Commodity.SOYBEAN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .isInstanceOf(CostProfileUnavailableException.class)
                .hasMessage("Nenhum perfil de custo disponível para a organização e commodity informadas.");
    }

    @Test
    @DisplayName("Should throw exception when freight profile is unavailable")
    void shouldThrowExceptionWhenFreightProfileIsUnavailable() {
        MarketQuote marketQuote = createMarketQuote(Commodity.SOYBEAN, "132.45", Currency.USD, Unit.TON, "CEPEA");
        ExchangeRate exchangeRate = createExchangeRate(Currency.USD, Currency.BRL, "5.421300", "Banco Central");
        CostProfile costProfile = createCostProfile(Commodity.SOYBEAN, "45.00");

        when(marketQuoteRepository.findTopByCommodityOrderByQuotedAtDesc(Commodity.SOYBEAN))
                .thenReturn(Optional.of(marketQuote));
        when(exchangeRateRepository.findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency.USD, Currency.BRL))
                .thenReturn(Optional.of(exchangeRate));
        when(costProfileRepository.findByOrganization_IdAndCommodity(ORGANIZATION_ID, Commodity.SOYBEAN))
                .thenReturn(Optional.of(costProfile));
        when(freightProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .isInstanceOf(FreightProfileUnavailableException.class)
                .hasMessage("Nenhum frete disponível para a organização, fazenda e commodity informadas.");
    }

    @Test
    @DisplayName("Should throw exception when commercial adjustment profile is unavailable")
    void shouldThrowExceptionWhenCommercialAdjustmentProfileIsUnavailable() {
        MarketQuote marketQuote = createMarketQuote(Commodity.SOYBEAN, "132.45", Currency.USD, Unit.TON, "CEPEA");
        ExchangeRate exchangeRate = createExchangeRate(Currency.USD, Currency.BRL, "5.421300", "Banco Central");
        CostProfile costProfile = createCostProfile(Commodity.SOYBEAN, "45.00");
        FreightProfile freightProfile = createFreightProfile(Commodity.SOYBEAN, "20.00");

        when(marketQuoteRepository.findTopByCommodityOrderByQuotedAtDesc(Commodity.SOYBEAN))
                .thenReturn(Optional.of(marketQuote));
        when(exchangeRateRepository.findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency.USD, Currency.BRL))
                .thenReturn(Optional.of(exchangeRate));
        when(costProfileRepository.findByOrganization_IdAndCommodity(ORGANIZATION_ID, Commodity.SOYBEAN))
                .thenReturn(Optional.of(costProfile));
        when(freightProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .thenReturn(Optional.of(freightProfile));
        when(commercialAdjustmentProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .isInstanceOf(CommercialAdjustmentProfileUnavailableException.class)
                .hasMessage("Nenhum ajuste comercial disponível para a organização, fazenda e commodity informadas.");
    }

    @Test
    @DisplayName("Should throw exception when market quote currency is not USD")
    void shouldThrowExceptionWhenMarketQuoteCurrencyIsNotUsd() {
        MarketQuote marketQuote = createMarketQuote(Commodity.SOYBEAN, "132.45", Currency.BRL, Unit.TON, "CEPEA");
        ExchangeRate exchangeRate = createExchangeRate(Currency.USD, Currency.BRL, "5.421300", "Banco Central");
        CostProfile costProfile = createCostProfile(Commodity.SOYBEAN, "45.00");
        FreightProfile freightProfile = createFreightProfile(Commodity.SOYBEAN, "20.00");
        CommercialAdjustmentProfile commercialAdjustmentProfile = createCommercialAdjustmentProfile(Commodity.SOYBEAN, "10.00");

        stubPricingDependencies(marketQuote, exchangeRate, costProfile, freightProfile, commercialAdjustmentProfile);

        assertThatThrownBy(() -> pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .isInstanceOf(UnsupportedPricingContextException.class)
                .hasMessage("O cálculo atual suporta apenas cotações em USD.");
    }

    @Test
    @DisplayName("Should throw exception when market quote unit is not TON")
    void shouldThrowExceptionWhenMarketQuoteUnitIsNotTon() {
        MarketQuote marketQuote = createMarketQuote(Commodity.SOYBEAN, "132.45", Currency.USD, null, "CEPEA");
        ExchangeRate exchangeRate = createExchangeRate(Currency.USD, Currency.BRL, "5.421300", "Banco Central");
        CostProfile costProfile = createCostProfile(Commodity.SOYBEAN, "45.00");
        FreightProfile freightProfile = createFreightProfile(Commodity.SOYBEAN, "20.00");
        CommercialAdjustmentProfile commercialAdjustmentProfile = createCommercialAdjustmentProfile(Commodity.SOYBEAN, "10.00");

        stubPricingDependencies(marketQuote, exchangeRate, costProfile, freightProfile, commercialAdjustmentProfile);

        assertThatThrownBy(() -> pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .isInstanceOf(UnsupportedPricingContextException.class)
                .hasMessage("O cálculo atual suporta apenas cotações por TON.");
    }

    @Test
    @DisplayName("Should throw exception when exchange rate pair is not USD to BRL")
    void shouldThrowExceptionWhenExchangeRatePairIsNotUsdToBrl() {
        MarketQuote marketQuote = createMarketQuote(Commodity.SOYBEAN, "132.45", Currency.USD, Unit.TON, "CEPEA");
        ExchangeRate exchangeRate = createExchangeRate(Currency.BRL, Currency.USD, "0.184500", "Banco Central");
        CostProfile costProfile = createCostProfile(Commodity.SOYBEAN, "45.00");
        FreightProfile freightProfile = createFreightProfile(Commodity.SOYBEAN, "20.00");
        CommercialAdjustmentProfile commercialAdjustmentProfile = createCommercialAdjustmentProfile(Commodity.SOYBEAN, "10.00");

        stubPricingDependencies(marketQuote, exchangeRate, costProfile, freightProfile, commercialAdjustmentProfile);

        assertThatThrownBy(() -> pricingService.calculateCurrentPrice(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .isInstanceOf(UnsupportedPricingContextException.class)
                .hasMessage("O cálculo atual suporta apenas câmbio USD para BRL.");
    }

    private void stubPricingDependencies(
            MarketQuote marketQuote,
            ExchangeRate exchangeRate,
            CostProfile costProfile,
            FreightProfile freightProfile,
            CommercialAdjustmentProfile commercialAdjustmentProfile
    ) {
        when(marketQuoteRepository.findTopByCommodityOrderByQuotedAtDesc(Commodity.SOYBEAN))
                .thenReturn(Optional.of(marketQuote));
        when(exchangeRateRepository.findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency.USD, Currency.BRL))
                .thenReturn(Optional.of(exchangeRate));
        when(costProfileRepository.findByOrganization_IdAndCommodity(ORGANIZATION_ID, Commodity.SOYBEAN))
                .thenReturn(Optional.of(costProfile));
        when(freightProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .thenReturn(Optional.of(freightProfile));
        when(commercialAdjustmentProfileRepository.findByOrganization_IdAndFarm_IdAndCommodity(ORGANIZATION_ID, FARM_ID, Commodity.SOYBEAN))
                .thenReturn(Optional.of(commercialAdjustmentProfile));
    }

    private MarketQuote createMarketQuote(Commodity commodity, String price, Currency currency, Unit unit, String source) {
        MarketQuote marketQuote = new MarketQuote();
        marketQuote.setCommodity(commodity);
        marketQuote.setPrice(new BigDecimal(price));
        marketQuote.setCurrency(currency);
        marketQuote.setUnit(unit);
        marketQuote.setSource(source);
        marketQuote.setQuotedAt(LocalDateTime.of(2026, 4, 7, 10, 0));
        return marketQuote;
    }

    private ExchangeRate createExchangeRate(Currency fromCurrency, Currency toCurrency, String rate, String source) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setFromCurrency(fromCurrency);
        exchangeRate.setToCurrency(toCurrency);
        exchangeRate.setRate(new BigDecimal(rate));
        exchangeRate.setSource(source);
        exchangeRate.setQuotedAt(LocalDateTime.of(2026, 4, 7, 10, 5));
        return exchangeRate;
    }

    private CostProfile createCostProfile(Commodity commodity, String costPerTon) {
        Organization organization = createOrganization();

        CostProfile costProfile = new CostProfile();
        costProfile.setOrganization(organization);
        costProfile.setCommodity(commodity);
        costProfile.setCostPerTon(new BigDecimal(costPerTon));
        return costProfile;
    }

    private FreightProfile createFreightProfile(Commodity commodity, String freightPerTon) {
        Organization organization = createOrganization();
        Farm farm = createFarm(organization);

        FreightProfile freightProfile = new FreightProfile();
        freightProfile.setOrganization(organization);
        freightProfile.setFarm(farm);
        freightProfile.setCommodity(commodity);
        freightProfile.setFreightPerTon(new BigDecimal(freightPerTon));
        return freightProfile;
    }

    private CommercialAdjustmentProfile createCommercialAdjustmentProfile(Commodity commodity, String adjustmentPerTon) {
        Organization organization = createOrganization();
        Farm farm = createFarm(organization);

        CommercialAdjustmentProfile commercialAdjustmentProfile = new CommercialAdjustmentProfile();
        commercialAdjustmentProfile.setOrganization(organization);
        commercialAdjustmentProfile.setFarm(farm);
        commercialAdjustmentProfile.setCommodity(commodity);
        commercialAdjustmentProfile.setAdjustmentPerTon(new BigDecimal(adjustmentPerTon));
        return commercialAdjustmentProfile;
    }

    private Organization createOrganization() {
        Organization organization = new Organization();
        organization.setId(ORGANIZATION_ID);
        return organization;
    }

    private Farm createFarm(Organization organization) {
        Farm farm = new Farm();
        farm.setId(FARM_ID);
        farm.setOrganization(organization);
        return farm;
    }
}
