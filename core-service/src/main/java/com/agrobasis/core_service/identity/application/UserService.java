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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserEmailAlreadyExistsException("O email " + request.email() + " já existe");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.VIEWER);
        user.setAccessStatus(UserAccessStatus.PENDING_ORGANIZATION_APPROVAL);
        user.setOrganization(null);

        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(UUID id) {
        User user = userRepository.findWithOrganizationById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsersByOrganization(UUID organizationId, Pageable pageable) {
        Page<User> users = userRepository.findAllByOrganization_Id(organizationId, pageable);
        return users.map(this::toResponse);
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findWithOrganizationById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        boolean emailExists = userRepository.existsByEmailAndIdNot(request.email(), id);
        if (emailExists) {
            throw new UserEmailAlreadyExistsException("O email " + request.email() + " já existe");
        }

        user.setName(request.name());
        user.setEmail(request.email());

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        userRepository.delete(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getAccessStatus(),
                user.getOrganization() != null ? user.getOrganization().getId() : null
        );
    }
}
