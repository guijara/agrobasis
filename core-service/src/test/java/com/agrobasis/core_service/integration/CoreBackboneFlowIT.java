package com.agrobasis.core_service.integration;

import com.agrobasis.core_service.cost.api.dto.CostProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.CostProfileResponse;
import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.CommercialAdjustmentProfileResponse;
import com.agrobasis.core_service.cost.api.dto.FreightProfileCreateRequest;
import com.agrobasis.core_service.cost.api.dto.FreightProfileResponse;
import com.agrobasis.core_service.cost.infrastructure.CommercialAdjustmentProfileRepository;
import com.agrobasis.core_service.cost.infrastructure.CostProfileRepository;
import com.agrobasis.core_service.cost.infrastructure.FreightProfileRepository;
import com.agrobasis.core_service.farm.api.dto.FarmCreateRequest;
import com.agrobasis.core_service.farm.api.dto.FarmResponse;
import com.agrobasis.core_service.farm.api.dto.PlotCreateRequest;
import com.agrobasis.core_service.farm.api.dto.PlotResponse;
import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
import com.agrobasis.core_service.farm.infrastructure.PlotRepository;
import com.agrobasis.core_service.identity.api.dto.LoginRequest;
import com.agrobasis.core_service.identity.api.dto.LoginResponse;
import com.agrobasis.core_service.identity.api.dto.MembershipRequestCreateRequest;
import com.agrobasis.core_service.identity.api.dto.MembershipRequestResponse;
import com.agrobasis.core_service.identity.api.dto.UserCreateRequest;
import com.agrobasis.core_service.identity.api.dto.UserResponse;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserAccessStatus;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.infrastructure.MembershipRequestRepository;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import com.agrobasis.core_service.market.api.dto.ExchangeRateCreateRequest;
import com.agrobasis.core_service.market.api.dto.ExchangeRateResponse;
import com.agrobasis.core_service.market.api.dto.MarketQuoteCreateRequest;
import com.agrobasis.core_service.market.api.dto.MarketQuoteResponse;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;
import com.agrobasis.core_service.market.infrastructure.ExchangeRateRepository;
import com.agrobasis.core_service.market.infrastructure.MarketQuoteRepository;
import com.agrobasis.core_service.market.infrastructure.integration.BcbExchangeRateClient;
import com.agrobasis.core_service.market.infrastructure.integration.CommodityMarketQuoteClient;
import com.agrobasis.core_service.market.infrastructure.integration.dto.ExternalExchangeRateData;
import com.agrobasis.core_service.market.infrastructure.integration.dto.ExternalCommodityQuoteData;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CoreBackboneFlowIT {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private CostProfileRepository costProfileRepository;

    @Autowired
    private CommercialAdjustmentProfileRepository commercialAdjustmentProfileRepository;

    @Autowired
    private FreightProfileRepository freightProfileRepository;

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
    private MembershipRequestRepository membershipRequestRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CommodityMarketQuoteClient marketQuoteClient;

    @Autowired
    private BcbExchangeRateClient exchangeRateClient;

    private Organization organization;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        commercialAdjustmentProfileRepository.deleteAll();
        costProfileRepository.deleteAll();
        freightProfileRepository.deleteAll();
        exchangeRateRepository.deleteAll();
        marketQuoteRepository.deleteAll();
        plotRepository.deleteAll();
        farmRepository.deleteAll();
        membershipRequestRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();

        organization = new Organization();
        organization.setName("AgroTech");
        organization.setCnpj("12.345.678/0001-90");
        organization.setLocation("Cuiaba");
        organization = organizationRepository.save(organization);

        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@agrotech.com");
        admin.setPassword(passwordEncoder.encode("SenhaForte123"));
        admin.setRole(UserRole.ADMIN);
        admin.setAccessStatus(UserAccessStatus.ACTIVE);
        admin.setOrganization(organization);
        userRepository.save(admin);

        reset(marketQuoteClient, exchangeRateClient);
    }

    @Test
    @DisplayName("Should complete secured backbone flow and return net pricing")
    void shouldCompleteSecuredBackboneFlowAndReturnNetPricing() {
        UserResponse viewer = registerViewer();
        MembershipRequestResponse membershipRequest = createMembershipRequest(viewer.id());
        String adminToken = login("admin@agrotech.com", "SenhaForte123");
        approveMembership(membershipRequest.id(), adminToken);
        String viewerToken = login("backbone@agrotech.com", "SenhaForte123");

        FarmResponse farm = createFarm(adminToken);
        PlotResponse plot = createPlot(farm.id(), adminToken);
        MarketQuoteResponse marketQuote = createMarketQuote(adminToken);
        ExchangeRateResponse exchangeRate = createExchangeRate(adminToken);
        CostProfileResponse costProfile = createCostProfile(adminToken);
        FreightProfileResponse freightProfile = createFreightProfile(farm.id(), adminToken);
        CommercialAdjustmentProfileResponse commercialAdjustmentProfile = createCommercialAdjustmentProfile(farm.id(), adminToken);

        CurrentPricingResponse pricing = restClient.get()
                .uri(builder -> builder.path("/api/pricing/current")
                        .queryParam("organizationId", organization.getId())
                        .queryParam("farmId", farm.id())
                        .queryParam("commodity", Commodity.SOYBEAN)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + viewerToken)
                .retrieve()
                .toEntity(CurrentPricingResponse.class)
                .getBody();

        assertThat(viewer.organizationId()).isNull();
        assertThat(plot.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(marketQuote.price()).isEqualByComparingTo("132.45");
        assertThat(exchangeRate.rate()).isEqualByComparingTo("5.421300");
        assertThat(costProfile.costPerTon()).isEqualByComparingTo("45.00");
        assertThat(freightProfile.freightPerTon()).isEqualByComparingTo("20.00");
        assertThat(commercialAdjustmentProfile.adjustmentPerTon()).isEqualByComparingTo("10.00");
        assertThat(pricing).isNotNull();
        assertThat(pricing.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(pricing.farmId()).isEqualTo(farm.id());
        assertThat(pricing.convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(pricing.adjustedPrice()).isEqualByComparingTo("673.05");
        assertThat(pricing.netPrice()).isEqualByComparingTo("653.05");
        assertThat(pricing.commercialPrice()).isEqualByComparingTo("643.05");
    }

    @Test
    @DisplayName("Should sync external market data and calculate pricing from persisted records")
    void shouldSyncExternalMarketDataAndCalculatePricingFromPersistedRecords() {
        String adminToken = login("admin@agrotech.com", "SenhaForte123");
        stubExternalMarketData();

        FarmResponse farm = createFarm(adminToken);
        CostProfileResponse costProfile = createCostProfile(adminToken);
        FreightProfileResponse freightProfile = createFreightProfile(farm.id(), adminToken);
        CommercialAdjustmentProfileResponse commercialAdjustmentProfile = createCommercialAdjustmentProfile(farm.id(), adminToken);
        MarketQuoteResponse marketQuote = syncMarketQuote(adminToken);
        ExchangeRateResponse exchangeRate = syncExchangeRate(adminToken);

        CurrentPricingResponse pricing = restClient.get()
                .uri(builder -> builder.path("/api/pricing/current")
                        .queryParam("organizationId", organization.getId())
                        .queryParam("farmId", farm.id())
                        .queryParam("commodity", Commodity.SOYBEAN)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .toEntity(CurrentPricingResponse.class)
                .getBody();

        assertThat(costProfile.costPerTon()).isEqualByComparingTo("45.00");
        assertThat(freightProfile.freightPerTon()).isEqualByComparingTo("20.00");
        assertThat(commercialAdjustmentProfile.adjustmentPerTon()).isEqualByComparingTo("10.00");
        assertThat(marketQuote.source()).isEqualTo("B3");
        assertThat(exchangeRate.source()).isEqualTo("BCB PTAX");
        assertThat(marketQuoteRepository.findTopByCommodityOrderByQuotedAtDesc(Commodity.SOYBEAN)).isPresent();
        assertThat(exchangeRateRepository.findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency.USD, Currency.BRL)).isPresent();
        assertThat(pricing).isNotNull();
        assertThat(pricing.farmId()).isEqualTo(farm.id());
        assertThat(pricing.convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(pricing.adjustedPrice()).isEqualByComparingTo("673.05");
        assertThat(pricing.netPrice()).isEqualByComparingTo("653.05");
        assertThat(pricing.commercialPrice()).isEqualByComparingTo("643.05");
        assertThat(pricing.marketQuote().source()).isEqualTo("B3");
        assertThat(pricing.exchangeRate().source()).isEqualTo("BCB PTAX");
    }

    private UserResponse registerViewer() {
        return restClient.post()
                .uri("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UserCreateRequest("Operador Backbone", "backbone@agrotech.com", "SenhaForte123"))
                .retrieve()
                .toEntity(UserResponse.class)
                .getBody();
    }

    private MembershipRequestResponse createMembershipRequest(java.util.UUID userId) {
        return restClient.post()
                .uri("/api/identity/membership-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MembershipRequestCreateRequest(userId, organization.getId()))
                .retrieve()
                .toEntity(MembershipRequestResponse.class)
                .getBody();
    }

    private void approveMembership(java.util.UUID requestId, String adminToken) {
        restClient.put()
                .uri("/api/identity/membership-requests/{id}/approve", requestId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .toBodilessEntity();
    }

    private String login(String email, String password) {
        LoginResponse response = restClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest(email, password))
                .retrieve()
                .toEntity(LoginResponse.class)
                .getBody();
        return response.accessToken();
    }

    private FarmResponse createFarm(String token) {
        return restClient.post()
                .uri("/api/farm")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new FarmCreateRequest("Fazenda Boa Terra", "Cuiaba", 1500.50, organization.getId()))
                .retrieve()
                .toEntity(FarmResponse.class)
                .getBody();
    }

    private PlotResponse createPlot(java.util.UUID farmId, String token) {
        return restClient.post()
                .uri("/api/plot")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new PlotCreateRequest("Talhao 01", 50.50, Commodity.SOYBEAN, farmId))
                .retrieve()
                .toEntity(PlotResponse.class)
                .getBody();
    }

    private MarketQuoteResponse createMarketQuote(String token) {
        return restClient.post()
                .uri("/api/market/quotes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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

    private ExchangeRateResponse createExchangeRate(String token) {
        return restClient.post()
                .uri("/api/market/exchange-rates")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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

    private MarketQuoteResponse syncMarketQuote(String token) {
        return restClient.post()
                .uri(builder -> builder.path("/api/market/quotes/sync")
                        .queryParam("commodity", Commodity.SOYBEAN)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toEntity(MarketQuoteResponse.class)
                .getBody();
    }

    private ExchangeRateResponse syncExchangeRate(String token) {
        return restClient.post()
                .uri(builder -> builder.path("/api/market/exchange-rates/sync")
                        .queryParam("fromCurrency", Currency.USD)
                        .queryParam("toCurrency", Currency.BRL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toEntity(ExchangeRateResponse.class)
                .getBody();
    }

    private void stubExternalMarketData() {
        when(marketQuoteClient.fetchLatestCommodityQuote(Commodity.SOYBEAN)).thenReturn(new ExternalCommodityQuoteData(
                Commodity.SOYBEAN,
                new BigDecimal("132.45"),
                Currency.USD,
                Unit.TON,
                "B3",
                LocalDateTime.of(2026, 4, 13, 10, 0)
        ));
        when(exchangeRateClient.fetchLatestExchangeRate(Currency.USD, Currency.BRL)).thenReturn(new ExternalExchangeRateData(
                Currency.USD,
                Currency.BRL,
                new BigDecimal("5.421300"),
                "BCB PTAX",
                LocalDateTime.of(2026, 4, 13, 10, 5)
        ));
    }

    private CostProfileResponse createCostProfile(String token) {
        return restClient.post()
                .uri("/api/cost/profiles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CostProfileCreateRequest(
                        organization.getId(),
                        Commodity.SOYBEAN,
                        new BigDecimal("45.00")
                ))
                .retrieve()
                .toEntity(CostProfileResponse.class)
                .getBody();
    }

    private FreightProfileResponse createFreightProfile(java.util.UUID farmId, String token) {
        return restClient.post()
                .uri("/api/cost/freight-profiles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new FreightProfileCreateRequest(
                        organization.getId(),
                        farmId,
                        Commodity.SOYBEAN,
                        new BigDecimal("20.00")
                ))
                .retrieve()
                .toEntity(FreightProfileResponse.class)
                .getBody();
    }

    private CommercialAdjustmentProfileResponse createCommercialAdjustmentProfile(java.util.UUID farmId, String token) {
        return restClient.post()
                .uri("/api/cost/commercial-adjustment-profiles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CommercialAdjustmentProfileCreateRequest(
                        organization.getId(),
                        farmId,
                        Commodity.SOYBEAN,
                        new BigDecimal("10.00")
                ))
                .retrieve()
                .toEntity(CommercialAdjustmentProfileResponse.class)
                .getBody();
    }

    @TestConfiguration
    static class MarketIntegrationTestConfiguration {

        @Bean
        @Primary
        CommodityMarketQuoteClient marketQuoteClient() {
            return mock(CommodityMarketQuoteClient.class);
        }

        @Bean
        @Primary
        BcbExchangeRateClient exchangeRateClient() {
            return mock(BcbExchangeRateClient.class);
        }
    }
}
