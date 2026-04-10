package com.agrobasis.core_service.identity.application;

import com.agrobasis.core_service.identity.api.dto.LoginRequest;
import com.agrobasis.core_service.identity.api.dto.LoginResponse;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.UserAccessStatus;
import com.agrobasis.core_service.identity.domain.exception.InvalidCredentialsException;
import com.agrobasis.core_service.identity.domain.exception.UserAccessNotAllowedException;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciais inválidas.");
        }

        if (user.getAccessStatus() != UserAccessStatus.ACTIVE) {
            throw new UserAccessNotAllowedException("Usuário ainda não possui acesso aprovado a uma organização.");
        }

        if (user.getOrganization() == null) {
            throw new UserAccessNotAllowedException("Usuário ainda não possui acesso aprovado a uma organização.");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpiration(token),
                user.getId(),
                user.getOrganization().getId(),
                user.getRole()
        );
    }
}
