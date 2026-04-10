package com.agrobasis.core_service.identity.integration;

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
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import com.agrobasis.core_service.shared.api.error.ErrorResponse;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserUseCaseIT {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipRequestRepository membershipRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Organization organization;
    private User admin;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        membershipRequestRepository.deleteAll();
        userRepository.deleteAll();
        organizationRepository.deleteAll();

        organization = new Organization();
        organization.setName("AgroTech");
        organization.setCnpj("12.345.678/0001-90");
        organization.setLocation("Cuiaba");
        organization = organizationRepository.save(organization);

        admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@agrotech.com");
        admin.setPassword(passwordEncoder.encode("SenhaForte123"));
        admin.setRole(UserRole.ADMIN);
        admin.setAccessStatus(UserAccessStatus.ACTIVE);
        admin.setOrganization(organization);
        admin = userRepository.save(admin);
    }

    @Test
    @DisplayName("Should register public user, request membership, block login until approval and allow login after approval")
    void shouldHandleMembershipApprovalLifecycle() {
        UserResponse createdUser = restClient.post()
                .uri("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UserCreateRequest("Guilherme", "guilherme@agrotech.com", "SenhaForte123"))
                .retrieve()
                .toEntity(UserResponse.class)
                .getBody();

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.role()).isEqualTo(UserRole.VIEWER);
        assertThat(createdUser.accessStatus()).isEqualTo(UserAccessStatus.PENDING_ORGANIZATION_APPROVAL);
        assertThat(createdUser.organizationId()).isNull();

        var blockedLogin = restClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("guilherme@agrotech.com", "SenhaForte123"))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                .toEntity(ErrorResponse.class);

        assertThat(blockedLogin.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        MembershipRequestResponse membershipRequest = restClient.post()
                .uri("/api/identity/membership-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new MembershipRequestCreateRequest(createdUser.id(), organization.getId()))
                .retrieve()
                .toEntity(MembershipRequestResponse.class)
                .getBody();

        assertThat(membershipRequest).isNotNull();
        assertThat(membershipRequest.status().name()).isEqualTo("PENDING");

        String adminToken = login("admin@agrotech.com", "SenhaForte123");

        MembershipRequestResponse approved = restClient.put()
                .uri("/api/identity/membership-requests/{id}/approve", membershipRequest.id())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .toEntity(MembershipRequestResponse.class)
                .getBody();

        assertThat(approved).isNotNull();
        assertThat(approved.status().name()).isEqualTo("APPROVED");

        LoginResponse loginResponse = restClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest("guilherme@agrotech.com", "SenhaForte123"))
                .retrieve()
                .toEntity(LoginResponse.class)
                .getBody();

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.organizationId()).isEqualTo(organization.getId());
        assertThat(loginResponse.role()).isEqualTo(UserRole.VIEWER);
    }

    private String login(String email, String password) {
        return restClient.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginRequest(email, password))
                .retrieve()
                .toEntity(LoginResponse.class)
                .getBody()
                .accessToken();
    }
}
