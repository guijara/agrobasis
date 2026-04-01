package com.agrobasis.core_service.user;

import com.agrobasis.core_service.organization.Organization;
import com.agrobasis.core_service.organization.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
