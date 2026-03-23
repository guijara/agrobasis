package com.agrobasis.core_service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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

    public OrganizationResponseDto getOrganization(UUID id){
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organização não encontrada."));

        return new OrganizationResponseDto(
                organization.getId(),
                organization.getName(),
                organization.getCnpj(),
                organization.getLocation(),
                organization.getCreatedAt()
        );
    }

    public Page<OrganizationResponseDto> getAllOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable)
                .map(org -> new OrganizationResponseDto(
                        org.getId(),
                        org.getName(),
                        org.getCnpj(),
                        org.getLocation(),
                        org.getCreatedAt()
                ));
    }
}
