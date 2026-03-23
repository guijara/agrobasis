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
        organization.setName("AgroTech");
        organization.setCnpj("00.000.000/0000-00");
        organization.setLocation("Cuiabá");

        // Act
        Organization savedOrganization = repository.save(organization);

        // Assert
        assertThat(savedOrganization.getId()).isNotNull();
        assertThat(savedOrganization.getName()).isEqualTo("AgroTech");
    }
}
