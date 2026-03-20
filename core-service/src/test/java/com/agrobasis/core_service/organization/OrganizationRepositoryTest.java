package com.agrobasis.core_service.organization;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrganizationRepositoryTest {
    @Autowired
    private OrganizationRepository repository;

    @Test
    void shouldSaveOrganizationSuccessfully() {
        // Arrange
        Organization organization = new Organization();
        organization.setName("Fazenda Santa Maria");
        organization.setCnpj("12.345.678/0001-90");
        organization.setLocation("Sorriso - MT");

        // Act
        Organization savedOrganization = repository.save(organization);

        // Assert
        assertThat(savedOrganization.getId()).isNotNull();
        assertThat(savedOrganization.getName()).isEqualTo("Fazenda Santa Maria");
    }
}
