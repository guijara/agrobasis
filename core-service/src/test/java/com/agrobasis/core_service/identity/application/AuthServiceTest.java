package com.agrobasis.core_service.identity.application;

import com.agrobasis.core_service.identity.api.dto.LoginRequest;
import com.agrobasis.core_service.identity.api.dto.LoginResponse;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserAccessStatus;
import com.agrobasis.core_service.identity.domain.UserRole;
import com.agrobasis.core_service.identity.domain.exception.InvalidCredentialsException;
import com.agrobasis.core_service.identity.domain.exception.UserAccessNotAllowedException;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import com.agrobasis.core_service.organization.domain.Organization;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() {
        User user = createUser(UserAccessStatus.ACTIVE);
        LoginRequest request = new LoginRequest(user.getEmail(), "Senha123");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("token");
        when(jwtService.getExpiration("token")).thenReturn(Instant.parse("2026-04-10T15:00:00Z"));

        LoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.userId()).isEqualTo(user.getId());
        assertThat(response.organizationId()).isEqualTo(user.getOrganization().getId());
        assertThat(response.role()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("Should fail when password is invalid")
    void shouldFailWhenPasswordIsInvalid() {
        User user = createUser(UserAccessStatus.ACTIVE);
        LoginRequest request = new LoginRequest(user.getEmail(), "wrong");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciais inválidas.");
    }

    @Test
    @DisplayName("Should fail when email does not exist")
    void shouldFailWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("missing@email.com", "Senha123")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciais inválidas.");
    }

    @Test
    @DisplayName("Should fail when user is pending")
    void shouldFailWhenUserIsPending() {
        User user = createUser(UserAccessStatus.PENDING_ORGANIZATION_APPROVAL);
        mockCredentials(user);

        assertThatThrownBy(() -> authService.login(new LoginRequest(user.getEmail(), "Senha123")))
                .isInstanceOf(UserAccessNotAllowedException.class)
                .hasMessage("Usuário ainda não possui acesso aprovado a uma organização.");
    }

    @Test
    @DisplayName("Should fail when user is rejected")
    void shouldFailWhenUserIsRejected() {
        User user = createUser(UserAccessStatus.REJECTED);
        mockCredentials(user);

        assertThatThrownBy(() -> authService.login(new LoginRequest(user.getEmail(), "Senha123")))
                .isInstanceOf(UserAccessNotAllowedException.class)
                .hasMessage("Usuário ainda não possui acesso aprovado a uma organização.");
    }

    @Test
    @DisplayName("Should fail when user is active but has no organization")
    void shouldFailWhenUserIsActiveButHasNoOrganization() {
        User user = createUser(UserAccessStatus.ACTIVE);
        user.setOrganization(null);
        mockCredentials(user);

        assertThatThrownBy(() -> authService.login(new LoginRequest(user.getEmail(), "Senha123")))
                .isInstanceOf(UserAccessNotAllowedException.class)
                .hasMessage("Usuário ainda não possui acesso aprovado a uma organização.");

        verify(jwtService, never()).generateToken(any(User.class));
    }

    private void mockCredentials(User user) {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Senha123", user.getPassword())).thenReturn(true);
    }

    private User createUser(UserAccessStatus accessStatus) {
        Organization organization = new Organization();
        organization.setId(UUID.randomUUID());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("admin@email.com");
        user.setPassword("hashed");
        user.setRole(UserRole.ADMIN);
        user.setAccessStatus(accessStatus);
        user.setOrganization(organization);
        return user;
    }
}
