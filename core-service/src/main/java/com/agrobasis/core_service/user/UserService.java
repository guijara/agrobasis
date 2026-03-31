package com.agrobasis.core_service.user;

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
            throw new UserAlreadyExistsException("O usuário com email "+request.email()+" já existe");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setPassword(request.password());
        user.setOrganization();
    }
}
