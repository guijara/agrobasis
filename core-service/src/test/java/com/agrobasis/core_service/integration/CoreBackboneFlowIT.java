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
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import com.agrobasis.core_service.pricing.api.dto.CurrentPricingResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private MembershipRequestRepository membershipRequestRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Organization organization;

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
    }

    @Test
    @DisplayName("Should complete secured backbone flow and return adjusted pricing")
    void shouldCompleteSecuredBackboneFlowAndReturnAdjustedPricing() {
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

        CurrentPricingResponse pricing = restClient.get()
                .uri(builder -> builder.path("/api/pricing/current")
                        .queryParam("organizationId", organization.getId())
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
        assertThat(pricing).isNotNull();
        assertThat(pricing.commodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(pricing.convertedPrice()).isEqualByComparingTo("718.05");
        assertThat(pricing.adjustedPrice()).isEqualByComparingTo("673.05");
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
}
