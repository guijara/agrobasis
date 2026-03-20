package com.agrobasis.core_service.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private OrganizationRepository organizationRepository;

    public void organizationService(OrganizationRepository organizationRepository){
        this.organizationRepository = organizationRepository;
    }

    public Organization createOrganization(CreateOrganizationDto createOrganizationDto){

        // Verifica se já existe uma organização baseando-se no CNPJ
        boolean exists = organizationRepository.existsByCnpj(createOrganizationDto.cnpj());
        if (exists){
            throw new OrganizationAlreadyExistsException
                    ("Organização com CNPJ "+createOrganizationDto.cnpj()+" já existe.");
        }

        // Cria uma nova organização caso já não exista
        Organization organization =  new Organization();
        organization.setName(createOrganizationDto.name());
        organization.setCnpj(createOrganizationDto.cnpj());
        organization.setLocation(createOrganizationDto.location());

        return organizationRepository.save(organization);
    }
}
