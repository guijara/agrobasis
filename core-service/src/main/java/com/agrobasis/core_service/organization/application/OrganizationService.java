package com.agrobasis.core_service.organization.application;

import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationAlreadyExistsException;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import com.agrobasis.core_service.organization.api.dto.OrganizationRequest;
import com.agrobasis.core_service.organization.api.dto.OrganizationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationResponse createOrganization(OrganizationRequest organizationRequest){

        boolean exists = organizationRepository.existsByCnpj(organizationRequest.cnpj());
        if (exists){
            throw new OrganizationAlreadyExistsException
                    ("Organização com CNPJ "+ organizationRequest.cnpj()+" já existe.");
        }

        Organization organization =  new Organization();
        organization.setName(organizationRequest.name());
        organization.setCnpj(organizationRequest.cnpj());
        organization.setLocation(organizationRequest.location());

        organizationRepository.save(organization);

        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getCnpj(),
                organization.getLocation(),
                organization.getCreatedAt()
        );
    }

    public OrganizationResponse getOrganization(UUID id){
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organização não encontrada."));

        return new OrganizationResponse(
                organization.getId(),
                organization.getName(),
                organization.getCnpj(),
                organization.getLocation(),
                organization.getCreatedAt()
        );
    }

    public Page<OrganizationResponse> getAllOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable)
                .map(org -> new OrganizationResponse(
                        org.getId(),
                        org.getName(),
                        org.getCnpj(),
                        org.getLocation(),
                        org.getCreatedAt()
                ));
    }
}
