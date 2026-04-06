package com.agrobasis.core_service.identity.application;

import com.agrobasis.core_service.identity.api.dto.UserCreateRequest;
import com.agrobasis.core_service.identity.api.dto.UserResponse;
import com.agrobasis.core_service.identity.api.dto.UserUpdateRequest;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.domain.exception.UserEmailAlreadyExistsException;
import com.agrobasis.core_service.identity.domain.exception.UserNotFoundException;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("createUser()")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() {
            UUID userId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            UserCreateRequest request = new UserCreateRequest(
                    "Guilherme",
                    "guilherme@email.com",
                    "Senha123",
                    UserRole.ADMIN,
                    organizationId
            );

            Organization organization = new Organization();
            organization.setId(organizationId);

            User savedUser = new User();
            savedUser.setId(userId);
            savedUser.setName(request.name());
            savedUser.setEmail(request.email());
            savedUser.setPassword(request.password());
            savedUser.setRole(request.role());
            savedUser.setOrganization(organization);

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            UserResponse result = userService.createUser(request);

            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.name()).isEqualTo("Guilherme");
            assertThat(result.email()).isEqualTo("guilherme@email.com");
            assertThat(result.role()).isEqualTo(UserRole.ADMIN);
            assertThat(result.organizationId()).isEqualTo(organizationId);
            verify(userRepository).existsByEmail(request.email());
            verify(organizationRepository).findById(organizationId);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            UUID organizationId = UUID.randomUUID();
            UserCreateRequest request = new UserCreateRequest(
                    "Guilherme",
                    "guilherme@email.com",
                    "Senha123",
                    UserRole.ADMIN,
                    organizationId
            );

            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(UserEmailAlreadyExistsException.class)
                    .hasMessage("O email guilherme@email.com já existe");

            verify(userRepository).existsByEmail(request.email());
            verify(organizationRepository, never()).findById(any(UUID.class));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when organization is not found")
        void shouldThrowExceptionWhenOrganizationIsNotFound() {
            UUID organizationId = UUID.randomUUID();
            UserCreateRequest request = new UserCreateRequest(
                    "Guilherme",
                    "guilherme@email.com",
                    "Senha123",
                    UserRole.ADMIN,
                    organizationId
            );

            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(OrganizationNotFoundException.class)
                    .hasMessage("Organização não encontrada.");

            verify(userRepository).existsByEmail(request.email());
            verify(organizationRepository).findById(organizationId);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("findUserById()")
    class FindUserByIdTests {

        @Test
        @DisplayName("Should return user when ID exists")
        void shouldReturnUserWhenIdExists() {
            UUID userId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();

            Organization organization = new Organization();
            organization.setId(organizationId);

            User user = new User();
            user.setId(userId);
            user.setName("Guilherme");
            user.setEmail("guilherme@email.com");
            user.setRole(UserRole.OPERATOR);
            user.setOrganization(organization);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            UserResponse result = userService.findUserById(userId);

            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.name()).isEqualTo("Guilherme");
            assertThat(result.email()).isEqualTo("guilherme@email.com");
            assertThat(result.role()).isEqualTo(UserRole.OPERATOR);
            assertThat(result.organizationId()).isEqualTo(organizationId);
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("Should throw exception when user is not found by ID")
        void shouldThrowExceptionWhenUserIsNotFoundById() {
            UUID userId = UUID.randomUUID();

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findUserById(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuário não encontrado.");

            verify(userRepository).findById(userId);
        }
    }

    @Nested
    @DisplayName("findAllUsersByOrganization()")
    class FindAllUsersByOrganizationTests {

        @Test
        @DisplayName("Should return paginated users by organization")
        void shouldReturnPaginatedUsersByOrganization() {
            UUID userId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Organization organization = new Organization();
            organization.setId(organizationId);

            User user = new User();
            user.setId(userId);
            user.setName("Guilherme");
            user.setEmail("guilherme@email.com");
            user.setRole(UserRole.VIEWER);
            user.setOrganization(organization);

            Page<User> users = new PageImpl<>(List.of(user), pageable, 1);

            when(userRepository.findAllByOrganization_Id(organizationId, pageable)).thenReturn(users);

            Page<UserResponse> result = userService.findAllUsersByOrganization(organizationId, pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().id()).isEqualTo(userId);
            assertThat(result.getContent().getFirst().organizationId()).isEqualTo(organizationId);
            verify(userRepository).findAllByOrganization_Id(organizationId, pageable);
        }
    }

    @Nested
    @DisplayName("updateUser()")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            UUID userId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest("Novo Nome", "novo@email.com");

            Organization organization = new Organization();
            organization.setId(organizationId);

            User user = new User();
            user.setId(userId);
            user.setName("Nome Antigo");
            user.setEmail("antigo@email.com");
            user.setRole(UserRole.ADMIN);
            user.setOrganization(organization);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(request.email(), userId)).thenReturn(false);
            when(userRepository.save(user)).thenReturn(user);

            UserResponse result = userService.updateUser(userId, request);

            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.name()).isEqualTo("Novo Nome");
            assertThat(result.email()).isEqualTo("novo@email.com");
            assertThat(result.role()).isEqualTo(UserRole.ADMIN);
            assertThat(result.organizationId()).isEqualTo(organizationId);
            verify(userRepository).findById(userId);
            verify(userRepository).existsByEmailAndIdNot(request.email(), userId);
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Should throw exception when updating missing user")
        void shouldThrowExceptionWhenUpdatingMissingUser() {
            UUID userId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest("Novo Nome", "novo@email.com");

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(userId, request))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuário não encontrado.");

            verify(userRepository).findById(userId);
            verify(userRepository, never()).existsByEmailAndIdNot(any(String.class), any(UUID.class));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when updated email already exists")
        void shouldThrowExceptionWhenUpdatedEmailAlreadyExists() {
            UUID userId = UUID.randomUUID();
            UUID organizationId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest("Novo Nome", "novo@email.com");

            Organization organization = new Organization();
            organization.setId(organizationId);

            User user = new User();
            user.setId(userId);
            user.setName("Nome Antigo");
            user.setEmail("antigo@email.com");
            user.setRole(UserRole.ADMIN);
            user.setOrganization(organization);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(request.email(), userId)).thenReturn(true);

            assertThatThrownBy(() -> userService.updateUser(userId, request))
                    .isInstanceOf(UserEmailAlreadyExistsException.class)
                    .hasMessage("O email novo@email.com já existe");

            verify(userRepository).findById(userId);
            verify(userRepository).existsByEmailAndIdNot(request.email(), userId);
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            UUID userId = UUID.randomUUID();

            User user = new User();
            user.setId(userId);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            userService.deleteUser(userId);

            verify(userRepository).findById(userId);
            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("Should throw exception when deleting missing user")
        void shouldThrowExceptionWhenDeletingMissingUser() {
            UUID userId = UUID.randomUUID();

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(userId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuário não encontrado.");

            verify(userRepository).findById(userId);
            verify(userRepository, never()).delete(any(User.class));
        }
    }
}
