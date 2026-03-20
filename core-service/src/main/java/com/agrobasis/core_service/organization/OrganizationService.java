package com.agrobasis.core_service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationResponseDto createOrganization(OrganizationCreateRequest organizationCreateRequest){

        boolean exists = organizationRepository.existsByCnpj(organizationCreateRequest.cnpj());
        if (exists){
            throw new OrganizationAlreadyExistsException
                    ("Organização com CNPJ "+ organizationCreateRequest.cnpj()+" já existe.");
        }

        Organization organization =  new Organization();
        organization.setName(organizationCreateRequest.name());
        organization.setCnpj(organizationCreateRequest.cnpj());
        organization.setLocation(organizationCreateRequest.location());

        organizationRepository.save(organization);

        return new OrganizationResponseDto(
                organization.getId(),
                organization.getName(),
                organization.getCnpj(),
                organization.getLocation(),
                organization.getCreatedAt()
        );
    }
}
