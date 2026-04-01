package com.agrobasis.core_service.user;

import com.agrobasis.core_service.config.ErrorResponse;
import com.agrobasis.core_service.organization.Organization;
import com.agrobasis.core_service.organization.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserUseCaseTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID savedOrgId;

    @BeforeEach
    void setUp() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        userRepository.deleteAll();
        organizationRepository.deleteAll();

        Organization org = new Organization();
        org.setName("AgroTech");
        org.setCnpj("12.345.678/0001-90");
        org.setLocation("Cuiabá");
        org = organizationRepository.save(org);

        this.savedOrgId = org.getId();
    }

    @Nested
    @DisplayName("Cenários de POST /api/user")
    class CreateUserScenarios {

        @Test
        @DisplayName("Deve criar um usuário com sucesso e não retornar a senha")
        void shouldCreateUserSuccessfully() {
            // Arrange
            UserRequestDto request = new UserRequestDto(
                    "Guilherme",
                    "guilherme@agrotech.com",
                    "SenhaForte123",
                    UserRole.ADMIN,
                    savedOrgId
            );

            // Act
            var response = restClient.post()
                    .uri("/api/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(UserResponseDto.class);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().id()).isNotNull();
            assertThat(response.getBody().name()).isEqualTo("Guilherme");
            assertThat(response.getBody().email()).isEqualTo("guilherme@agrotech.com");
            assertThat(response.getBody().role()).isEqualTo(UserRole.ADMIN);
            assertThat(response.getBody().organizationId()).isEqualTo(savedOrgId);
        }

        @Test
        @DisplayName("Deve barrar a criação de usuário com e-mail já existente")
        void shouldFailWhenEmailIsDuplicated() {
            // Arrange
            User existingUser = new User();
            existingUser.setName("João");
            existingUser.setEmail("guilherme@agrotech.com");
            existingUser.setPassword("123456");
            existingUser.setRole(UserRole.OPERATOR);
            existingUser.setOrganization(organizationRepository.getReferenceById(savedOrgId));
            userRepository.save(existingUser);

            UserRequestDto conflictRequest = new UserRequestDto(
                    "Guilherme Clone",
                    "guilherme@agrotech.com",
                    "NovaSenha123",
                    UserRole.ADMIN,
                    savedOrgId
            );

            // Act
            var response = restClient.post()
                    .uri("/api/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(conflictRequest)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                    .toEntity(ErrorResponse.class);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().message()).isEqualTo("O email guilherme@agrotech.com já existe");
        }
    }
}