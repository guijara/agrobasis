package com.agrobasis.core_service.identity;

import com.agrobasis.core_service.shared.api.error.ErrorResponse;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import com.agrobasis.core_service.identity.api.dto.UserRequest;
import com.agrobasis.core_service.identity.api.dto.UserResponse;
import com.agrobasis.core_service.identity.api.dto.UserUpdateRequest;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
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
            UserRequest request = new UserRequest(
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
                    .toEntity(UserResponse.class);

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

            UserRequest conflictRequest = new UserRequest(
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

    @Nested
    @DisplayName("Cenários de GET /api/user")
    class GetUserScenarios {

        @Test
        @DisplayName("Deve buscar um usuário pelo ID com sucesso")
        void shouldGetUserById() {
            // Arrange
            User testUser = createTestUser("Buscado da Silva", "busca@agrotech.com");

            // Act
            var response = restClient.get()
                    .uri("/api/user/{id}", testUser.getId())
                    .retrieve()
                    .toEntity(UserResponse.class);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().name()).isEqualTo("Buscado da Silva");
            assertThat(response.getBody().email()).isEqualTo("busca@agrotech.com");
            assertThat(response.getBody().organizationId()).isEqualTo(savedOrgId);
        }

        @Test
        @DisplayName("Deve retornar 404 ao buscar usuário inexistente")
        void shouldReturn404WhenUserNotFound() {
            var response = restClient.get()
                    .uri("/api/user/{id}", UUID.randomUUID())
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                    .toEntity(ErrorResponse.class);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Deve listar usuários de uma organização com paginação")
        void shouldListUsersByOrganization() {
            // Arrange
            createTestUser("Alice Souza", "alice@agrotech.com");
            createTestUser("Bob Alves", "bob@agrotech.com");

            // Act
            var response = restClient.get()
                    .uri(builder -> builder.path("/api/user")
                            .queryParam("organizationId", savedOrgId)
                            .build())
                    .retrieve()
                    .toEntity(String.class);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).contains("Alice Souza");
            assertThat(response.getBody()).contains("Bob Alves");
        }
    }

    @Nested
    @DisplayName("Cenários de PUT /api/user/{id}")
    class UpdateUserScenarios {

        @Test
        @DisplayName("Deve atualizar os dados básicos do usuário com sucesso")
        void shouldUpdateUserSuccessfully() {
            // Arrange
            User existingUser = createTestUser("Usuário Antigo", "antigo@agrotech.com");

            UserUpdateRequest updateRequest = new UserUpdateRequest(
                    "Usuário Atualizado Silva",
                    "atualizado@agrotech.com"
            );

            // Act
            var response = restClient.put()
                    .uri("/api/user/{id}", existingUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateRequest)
                    .retrieve()
                    .toEntity(UserResponse.class);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().name()).isEqualTo("Usuário Atualizado Silva");
            assertThat(response.getBody().email()).isEqualTo("atualizado@agrotech.com");
        }

        @Test
        @DisplayName("Deve barrar atualização se o novo e-mail já pertencer a OUTRO usuário")
        void shouldFailUpdateWhenEmailBelongsToAnotherUser() {
            // Arrange
            createTestUser("Alice Original", "alice@agrotech.com");
            User userBob = createTestUser("Bob Hacker", "bob@agrotech.com");

            UserUpdateRequest conflictRequest = new UserUpdateRequest(
                    "Bob Malicioso",
                    "alice@agrotech.com"
            );

            // Act
            var response = restClient.put()
                    .uri("/api/user/{id}", userBob.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(conflictRequest)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {})
                    .toEntity(ErrorResponse.class);

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody().message()).isEqualTo("O email alice@agrotech.com já existe");
        }
    }

    @Nested
    @DisplayName("Cenários de DELETE /api/user")
    class DeleteUserScenarios {

        @Test
        @DisplayName("Deve deletar um usuário com sucesso (Status 204)")
        void shouldDeleteUserSuccessfully() {
            // Arrange
            User testUser = createTestUser("Demitido da Silva", "demitido@agrotech.com");

            // Act
            var response = restClient.delete()
                    .uri("/api/user/{id}", testUser.getId())
                    .retrieve()
                    .toBodilessEntity();

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

            boolean exists = userRepository.existsById(testUser.getId());
            assertThat(exists).isFalse();
        }
    }

    private User createTestUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("Senha123");
        user.setRole(UserRole.OPERATOR);
        user.setOrganization(organizationRepository.getReferenceById(savedOrgId));
        return userRepository.save(user);
    }
}