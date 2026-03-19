package com.agrobasis.core_service.organization;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
public class OrganizationRepositoryTest {
    @Autowired
    private OrganizationRepository repository;

    @Test
    void shouldSaveOrganizationSuccessfully() {
        // Arrange (Preparação)
        Organization organization = new Organization();
        organization.setName("Fazenda Santa Maria");
        organization.setCnpj("12.345.678/0001-90");
        organization.setLocation("Sorriso - MT");

        // Act (Ação)
        Organization savedOrganization = repository.save(organization);

        // Assert (Verificação)
        assertThat(savedOrganization.getId()).isNotNull();
        assertThat(savedOrganization.getName()).isEqualTo("Fazenda Santa Maria");
    }
}
