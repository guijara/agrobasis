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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TenantEnforcementIT {

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
    private Organization organizationOne;
    private Organization organizationTwo;

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

        organizationOne = createOrganization("Org One", "11.111.111/0001-11");
        organizationTwo = createOrganization("Org Two", "22.222.222/0001-22");
    }

    @Test
    @DisplayName("Should deny access when organizationId differs from token tenant")
    void shouldDenyAccessWhenOrganizationDiffersFromTokenTenant() {
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@orgone.com");
        admin.setPassword(passwordEncoder.encode("Senha123"));
        admin.setRole(UserRole.ADMIN);
        admin.setAccessStatus(UserAccessStatus.ACTIVE);
        admin.setOrganization(organizationOne);
        admin = userRepository.save(admin);

        String token = jwtService.generateToken(admin);

        var response = restClient.get()
                .uri(builder -> builder.path("/api/cost/profiles")
                        .queryParam("organizationId", organizationTwo.getId())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private Organization createOrganization(String name, String cnpj) {
        Organization organization = new Organization();
        organization.setName(name);
        organization.setCnpj(cnpj);
        organization.setLocation("Cuiaba");
        return organizationRepository.save(organization);
    }
}
