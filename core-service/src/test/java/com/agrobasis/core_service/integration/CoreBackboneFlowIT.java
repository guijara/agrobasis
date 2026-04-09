package com.agrobasis.core_service.integration;

import com.agrobasis.core_service.cost.api.dto.CostProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.CostProfileResponse;
import com.agrobasis.core_service.cost.infrastructure.CostProfileRepository;
import com.agrobasis.core_service.farm.api.dto.FarmCreateRequest;
import com.agrobasis.core_service.farm.api.dto.FarmResponse;
import com.agrobasis.core_service.farm.api.dto.PlotCreateRequest;
import com.agrobasis.core_service.farm.api.dto.PlotResponse;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
import com.agrobasis.core_service.farm.infrastructure.PlotRepository;
import com.agrobasis.core_service.identity.api.dto.UserCreateRequest;
import com.agrobasis.core_service.identity.api.dto.UserResponse;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import com.agrobasis.core_service.market.api.dto.ExchangeRateCreateRequest;
import com.agrobasis.core_service.market.api.dto.ExchangeRateResponse;
import com.agrobasis.core_service.market.api.dto.MarketQuoteCreateRequest;
import com.agrobasis.core_service.market.api.dto.MarketQuoteResponse;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;
import com.agrobasis.core_service.market.infrastructure.ExchangeRateRepository;
import com.agrobasis.core_service.market.infrastructure.MarketQuoteRepository;
import com.agrobasis.core_service.organization.api.dto.OrganizationRequest;
import com.agrobasis.core_service.organization.api.dto.OrganizationResponse;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CoreBackboneFlowIT {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private CostProfileRepository costProfileRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private MarketQuoteRepository marketQuoteRepository;

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        costProfileRepository.deleteAll();
        exchangeRateRepository.deleteAll();
        marketQuoteRepository.deleteAll();
        plotRepository.deleteAll();
        farmRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();
    }

    @Test
    @DisplayName("Should complete the core backbone flow and return adjusted pricing")
    void shouldCompleteCoreBackboneFlowAndReturnAdjustedPricing() {
        OrganizationResponse organization = createOrganization();
        UserResponse user = createUser(organization.id());
        FarmResponse farm = createFarm(organization.id());
        PlotResponse plot = createPlot(farm.id());
        MarketQuoteResponse marketQuote = createMarketQuote();
        ExchangeRateResponse exchangeRate = createExchangeRate();
        CostProfileResponse costProfile = createCostProfile(organization.id());

        CurrentPricingResponse pricing = restClient.get()
                .uri(builder -> builder.path("/api/pricing/current")
                        .queryParam("organizationId", organization.id())
                        .queryParam("commodity", Commodity.SOYBEAN)
                        .build())
                .retrieve()
                .toEntity(CurrentPricingResponse.class)
                .getBody();

        assertThat(user.organizationId()).isEqualTo(organization.id());
        assertThat(plot.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(marketQuote.price()).isEqualByComparingTo("132.45");
        assertThat(exchangeRate.rate()).isEqualByComparingTo("5.421300");
        assertThat(costProfile.costPerTon()).isEqualByComparingTo("45.00");
        assertThat(pricing).isNotNull();
        assertThat(pricing.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(pricing.convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(pricing.costPerTon()).isEqualByComparingTo("45.00");
        assertThat(pricing.adjustedPrice()).isEqualByComparingTo("673.05");
        assertThat(pricing.targetCurrency()).isEqualTo(Currency.BRL);
        assertThat(pricing.unit()).isEqualTo(Unit.TON);
        assertThat(pricing.marketQuote().price()).isEqualByComparingTo("132.45");
        assertThat(pricing.exchangeRate().rate()).isEqualByComparingTo("5.421300");
        assertThat(pricing.calculationMemory().conversionFormula()).isEqualTo("price_in_usd_per_ton × usd_brl_rate");
        assertThat(pricing.calculationMemory().adjustmentFormula()).isEqualTo("converted_price - cost_per_ton");
        assertThat(pricing.calculationMemory().costPerTon()).isEqualByComparingTo("45.00");
        assertThat(pricing.calculationMemory().convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(pricing.calculationMemory().adjustedPrice()).isEqualByComparingTo("673.05");
        assertThat(pricing.calculatedAt()).isNotNull();
    }

    private OrganizationResponse createOrganization() {
        return restClient.post()
                .uri("/api/organization")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new OrganizationRequest("AgroTech", "12.345.678/0001-90", "Cuiaba"))
                .retrieve()
                .toEntity(OrganizationResponse.class)
                .getBody();
    }

    private UserResponse createUser(java.util.UUID organizationId) {
        return restClient.post()
                .uri("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UserCreateRequest(
                        "Operador Backbone",
                        "backbone+" + System.nanoTime() + "@agrotech.com",
                        "SenhaForte123",
                        UserRole.ADMIN,
                        organizationId
                ))
                .retrieve()
                .toEntity(UserResponse.class)
                .getBody();
    }

    private FarmResponse createFarm(java.util.UUID organizationId) {
        return restClient.post()
                .uri("/api/farm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new FarmCreateRequest("Fazenda Boa Terra", "Cuiaba", 1500.50, organizationId))
                .retrieve()
                .toEntity(FarmResponse.class)
                .getBody();
    }

    private PlotResponse createPlot(java.util.UUID farmId) {
        return restClient.post()
                .uri("/api/plot")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new PlotCreateRequest("Talhao 01", 50.50, Commodity.SOYBEAN, farmId))
                .retrieve()
                .toEntity(PlotResponse.class)
                .getBody();
    }

    private MarketQuoteResponse createMarketQuote() {
        return restClient.post()
                .uri("/api/market/quotes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MarketQuoteCreateRequest(
                        Commodity.SOYBEAN,
                        "CEPEA",
                        new BigDecimal("132.45"),
                        Currency.USD,
                        Unit.TON,
                        LocalDateTime.of(2026, 4, 7, 10, 0)
                ))
                .retrieve()
                .toEntity(MarketQuoteResponse.class)
                .getBody();
    }

    private ExchangeRateResponse createExchangeRate() {
        return restClient.post()
                .uri("/api/market/exchange-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ExchangeRateCreateRequest(
                        Currency.USD,
                        Currency.BRL,
                        new BigDecimal("5.421300"),
                        "Banco Central",
                        LocalDateTime.of(2026, 4, 7, 10, 5)
                ))
                .retrieve()
                .toEntity(ExchangeRateResponse.class)
                .getBody();
    }

    private CostProfileResponse createCostProfile(java.util.UUID organizationId) {
        return restClient.post()
                .uri("/api/cost/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CostProfileCreateRequest(
                        organizationId,
                        Commodity.SOYBEAN,
                        new BigDecimal("45.00")
                ))
                .retrieve()
                .toEntity(CostProfileResponse.class)
                .getBody();
    }
}
