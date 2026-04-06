package com.agrobasis.core_service.identity.application;

import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import com.agrobasis.core_service.identity.api.dto.UserRequest;
import com.agrobasis.core_service.identity.api.dto.UserResponse;
import com.agrobasis.core_service.identity.api.dto.UserUpdateRequest;
import com.agrobasis.core_service.identity.domain.User;
import com.agrobasis.core_service.identity.domain.exception.UserEmailAlreadyExistsException;
import com.agrobasis.core_service.identity.domain.exception.UserNotFoundException;
import com.agrobasis.core_service.identity.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public UserResponse createUser(UserRequest request){
        boolean exists = userRepository.existsByEmail(request.email());

        if (exists){
            throw new UserEmailAlreadyExistsException("O email "+request.email()+" já existe");
        }

        Organization organization = organizationRepository.findById(request.organizationId()).orElseThrow(
                () -> new OrganizationNotFoundException("Organização não encontrada."));

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setPassword(request.password());
        user.setOrganization(organization);

        User savedUser = userRepository.save(user);

        return new UserResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole(),
            savedUser.getOrganization().getId()
        );
    }

    public UserResponse findUserById(UUID id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Usuário não encontrado."));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getOrganization().getId()
        );
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsersByOrganization(UUID organizationId, Pageable pageable){
        Page<User> users = userRepository.findAllByOrganization_Id(organizationId,pageable);

        return users.map(user -> new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getOrganization().getId()
        ));
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Usuário não encontrado."));

        boolean emailExists = userRepository.existsByEmailAndIdNot(request.email(),id);

        if (emailExists){
            throw new UserEmailAlreadyExistsException("O email "+request.email()+" já existe");
        }

        user.setName(request.name());
        user.setEmail(request.email());

        userRepository.save(user);

        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getOrganization().getId()
        );
    }

    @Transactional
    public void deleteUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Usuário não encontrado."));

        userRepository.delete(user);
    }
}
