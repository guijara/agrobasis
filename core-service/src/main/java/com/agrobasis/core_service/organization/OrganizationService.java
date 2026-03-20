package com.agrobasis.core_service.organization;

import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    private OrganizationRepository organizationRepository;

    public void organizationService(OrganizationRepository organizationRepository){
        this.organizationRepository = organizationRepository;
    }

    public void createOrganization(CreateOrganizationDto createOrganizationDto){

        // Verifica se já existe uma organização baseando-se no CNPJ
        Organization organization = organizationRepository.
                existsOrganizationByCnpj(createOrganizationDto.cnpj())
                .orElseThrow(() -> new OrganizationAlreadyExistsException
                        ("Organização com CNPJ "+createOrganizationDto.cnpj()+" já existe."));


    }
}
