package com.agrobasis.core_service.identity.application;

import com.agrobasis.core_service.identity.api.dto.UserCreateRequest;
import com.agrobasis.core_service.identity.api.dto.UserResponse;
import com.agrobasis.core_service.identity.api.dto.UserUpdateRequest;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserAccessStatus;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.domain.exception.UserEmailAlreadyExistsException;
import com.agrobasis.core_service.identity.domain.exception.UserNotFoundException;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import com.agrobasis.core_service.organization.domain.Organization;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should create user with hashed password and pending access")
    void shouldCreateUserWithHashedPasswordAndPendingAccess() {
        UserCreateRequest request = new UserCreateRequest("Guilherme", "guilherme@email.com", "Senha123");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        UserResponse result = userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertThat(savedUser.getPassword()).isEqualTo("hashed-password");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.VIEWER);
        assertThat(savedUser.getAccessStatus()).isEqualTo(UserAccessStatus.PENDING_ORGANIZATION_APPROVAL);
        assertThat(savedUser.getOrganization()).isNull();
        assertThat(result.role()).isEqualTo(UserRole.VIEWER);
        assertThat(result.accessStatus()).isEqualTo(UserAccessStatus.PENDING_ORGANIZATION_APPROVAL);
        assertThat(result.organizationId()).isNull();
    }

    @Test
    @DisplayName("Should reject duplicated email")
    void shouldRejectDuplicatedEmail() {
        UserCreateRequest request = new UserCreateRequest("Guilherme", "guilherme@email.com", "Senha123");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(UserEmailAlreadyExistsException.class)
                .hasMessage("O email guilherme@email.com já existe");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return user by id")
    void shouldReturnUserById() {
        User user = createUser();
        when(userRepository.findWithOrganizationById(user.getId())).thenReturn(Optional.of(user));

        UserResponse result = userService.findUserById(user.getId());

        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.organizationId()).isEqualTo(user.getOrganization().getId());
        assertThat(result.accessStatus()).isEqualTo(UserAccessStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throw when user is not found")
    void shouldThrowWhenUserIsNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findWithOrganizationById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Usuário não encontrado.");
    }

    @Test
    @DisplayName("Should list users by organization")
    void shouldListUsersByOrganization() {
        User user = createUser();
        PageRequest pageable = PageRequest.of(0, 10);
        when(userRepository.findAllByOrganization_Id(user.getOrganization().getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(user), pageable, 1));

        Page<UserResponse> result = userService.findAllUsersByOrganization(user.getOrganization().getId(), pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().email()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() {
        User user = createUser();
        UserUpdateRequest request = new UserUpdateRequest("Novo Nome", "novo@email.com");
        when(userRepository.findWithOrganizationById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot(request.email(), user.getId())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userService.updateUser(user.getId(), request);

        assertThat(result.name()).isEqualTo("Novo Nome");
        assertThat(result.email()).isEqualTo("novo@email.com");
    }

    @Test
    @DisplayName("Should delete existing user")
    void shouldDeleteExistingUser() {
        User user = createUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getId());

        verify(userRepository).delete(user);
    }

    private User createUser() {
        Organization organization = new Organization();
        organization.setId(UUID.randomUUID());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Guilherme");
        user.setEmail("guilherme@email.com");
        user.setPassword("hashed-password");
        user.setRole(UserRole.ADMIN);
        user.setAccessStatus(UserAccessStatus.ACTIVE);
        user.setOrganization(organization);
        return user;
    }
}
