package com.agrobasis.core_service.user;

import com.agrobasis.core_service.organization.Organization;
import com.agrobasis.core_service.organization.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.OrganizationRepository;
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

    public UserResponseDto createUser(UserRequestDto request){
        boolean exists = userRepository.existsByEmail(request.email());

        if (exists){
            throw new UserEmailAlreadyExistsException("O email "+request.email()+" já existe");
        }

        Organization organization = organizationRepository.findById(request.organizationId()).orElseThrow(
                () -> new OrganizationNotFoundException("A Organização não encontrada."));

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setPassword(request.password());
        user.setOrganization(organization);

        userRepository.save(user);

        return new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getOrganization().getId()
        );
    }

    public UserResponseDto findUserById(UUID id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Usuário não encontrado."));

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getOrganization().getId()
        );
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDto> findAllUsersByOrganization(UUID organizationId, Pageable pageable){
        Page<User> users = userRepository.findAllByOrganization_Id(organizationId,pageable);

        return users.map(user -> new UserResponseDto(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getOrganization().getId()
        ));
    }

    @Transactional
    public UserResponseDto updateUser(UUID id, UserUpdateRequestDto request){
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Usuário não encontrado"));

        boolean emailExists = userRepository.existsByEmailAndIdNot(request.email(),id);

        if (emailExists){
            throw new UserEmailAlreadyExistsException("O email "+request.email()+" já existe");
        }

        user.setName(request.name());
        user.setEmail(request.email());

        userRepository.save(user);

        return new UserResponseDto(
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
