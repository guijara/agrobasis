package com.agrobasis.core_service.integration.security;

import com.agrobasis.core_service.cost.infrastructure.CostProfileRepository;
import com.agrobasis.core_service.cost.infrastructure.FreightProfileRepository;
import com.agrobasis.core_service.farm.infrastructure.FarmRepository;
import com.agrobasis.core_service.farm.infrastructure.PlotRepository;
import com.agrobasis.core_service.identity.application.JwtService;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserAccessStatus;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.infrastructure.MembershipRequestRepository;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import com.agrobasis.core_service.market.infrastructure.ExchangeRateRepository;
import com.agrobasis.core_service.market.infrastructure.MarketQuoteRepository;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityHttpIT {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipRequestRepository membershipRequestRepository;

    @Autowired
    private CostProfileRepository costProfileRepository;

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
    private OrganizationRepository organizationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private RestClient restClient;
    private Organization organization;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

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
        organization.setCnpj("98.765.432/0001-10");
        organization.setLocation("Cuiaba");
        organization = organizationRepository.save(organization);
    }

    @Test
    @DisplayName("Should return 401 when protected endpoint is called without token")
    void shouldReturn401WithoutToken() {
        var response = restClient.get()
                .uri(builder -> builder.path("/api/cost/profiles")
                        .queryParam("organizationId", organization.getId())
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 401 when token is invalid")
    void shouldReturn401WithInvalidToken() {
        var response = restClient.get()
                .uri(builder -> builder.path("/api/cost/profiles")
                        .queryParam("organizationId", organization.getId())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 403 when role is insufficient")
    void shouldReturn403WhenRoleIsInsufficient() {
        User viewer = saveUser("viewer@agro.com", UserRole.VIEWER, organization);
        String token = jwtService.generateToken(viewer);

        var response = restClient.post()
                .uri("/api/cost/profiles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "organizationId": "%s",
                          "commodity": "SOYBEAN",
                          "costPerTon": 45.00
                        }
                        """.formatted(organization.getId()))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should return 401 when market quote sync endpoint is called without token")
    void shouldReturn401WhenMarketQuoteSyncEndpointIsCalledWithoutToken() {
        var response = restClient.post()
                .uri(builder -> builder.path("/api/market/quotes/sync")
                        .queryParam("commodity", "SOYBEAN")
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 401 when exchange rate sync endpoint is called without token")
    void shouldReturn401WhenExchangeRateSyncEndpointIsCalledWithoutToken() {
        var response = restClient.post()
                .uri(builder -> builder.path("/api/market/exchange-rates/sync")
                        .queryParam("fromCurrency", "USD")
                        .queryParam("toCurrency", "BRL")
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should return 403 when market quote sync role is insufficient")
    void shouldReturn403WhenMarketQuoteSyncRoleIsInsufficient() {
        User viewer = saveUser("quote-sync-viewer@agro.com", UserRole.VIEWER, organization);
        String token = jwtService.generateToken(viewer);

        var response = restClient.post()
                .uri(builder -> builder.path("/api/market/quotes/sync")
                        .queryParam("commodity", "SOYBEAN")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("Should return 403 when exchange rate sync role is insufficient")
    void shouldReturn403WhenExchangeRateSyncRoleIsInsufficient() {
        User viewer = saveUser("exchange-sync-viewer@agro.com", UserRole.VIEWER, organization);
        String token = jwtService.generateToken(viewer);

        var response = restClient.post()
                .uri(builder -> builder.path("/api/market/exchange-rates/sync")
                        .queryParam("fromCurrency", "USD")
                        .queryParam("toCurrency", "BRL")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private User saveUser(String email, UserRole role, Organization organization) {
        User user = new User();
        user.setName("User");
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("Senha123"));
        user.setRole(role);
        user.setAccessStatus(UserAccessStatus.ACTIVE);
        user.setOrganization(organization);
        return userRepository.save(user);
    }
}
