package com.agrobasis.core_service.organization;

import com.agrobasis.core_service.organization.api.dto.OrganizationRequest;
import com.agrobasis.core_service.organization.api.dto.OrganizationResponse;
import com.agrobasis.core_service.organization.application.OrganizationService;
import com.agrobasis.core_service.organization.domain.Organization;
import com.agrobasis.core_service.organization.domain.exception.OrganizationAlreadyExistsException;
import com.agrobasis.core_service.organization.domain.exception.OrganizationNotFoundException;
import com.agrobasis.core_service.organization.infrastructure.OrganizationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @Nested
    @DisplayName("createOrganization()")
    class CreateOrganizationTests {

        @Test
        void shouldCreateOrganizationSuccessfully() {
            // Arrange
            OrganizationRequest dto = new OrganizationRequest("AgroTech", "00.000.000/0000-00", "Cuiabá");

            when(organizationRepository.existsByCnpj(dto.cnpj())).thenReturn(false);

            Organization savedEntity = new Organization();
            savedEntity.setName(dto.name());
            savedEntity.setCnpj(dto.cnpj());
            when(organizationRepository.save(any(Organization.class))).thenReturn(savedEntity);

            // Act
            OrganizationResponse result = organizationService.createOrganization(dto);

            // Assert
            assertThat(result.name()).isEqualTo("AgroTech");
            verify(organizationRepository, times(1)).save(any(Organization.class));
        }

        @Test
        void shouldThrowExceptionWhenCnpjAlreadyExists() {
            // Arrange
            OrganizationRequest dto = new OrganizationRequest("AgroTech", "00.000.000/0000-00", "Cuiabá");

            when(organizationRepository.existsByCnpj(dto.cnpj())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> organizationService.createOrganization(dto))
                    .isInstanceOf(OrganizationAlreadyExistsException.class)
                    .hasMessage("Organização com CNPJ 00.000.000/0000-00 já existe.");

            verify(organizationRepository, never()).save(any(Organization.class));
        }
    }

    @Nested
    @DisplayName("getOrganization()")
    class GetOrganizationTests {

        @Test
        void shouldReturnOrganizationWhenIdExists() {
            // Arrange
            UUID id = UUID.randomUUID();
            Organization organization = new Organization();
            organization.setId(id);
            organization.setName("AgroTech");
            organization.setCnpj("00.000.000/0000-00");
            organization.setLocation("Cuiabá");

            when(organizationRepository.findById(id)).thenReturn(Optional.of(organization));

            // Act
            OrganizationResponse result = organizationService.getOrganization(id);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(id);
            assertThat(result.name()).isEqualTo("AgroTech");
            assertThat(result.cnpj()).isEqualTo("00.000.000/0000-00");
            assertThat(result.location()).isEqualTo("Cuiabá");

            verify(organizationRepository, times(1)).findById(id);
        }

        @Test
        void shouldThrowExceptionWhenIdDoesNotExist() {
            // Arrange
            UUID id = UUID.randomUUID();

            when(organizationRepository.findById(id)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> organizationService.getOrganization(id))
                    .isInstanceOf(OrganizationNotFoundException.class)
                    .hasMessage("Organização não encontrada.");

            verify(organizationRepository, times(1)).findById(id);
        }
    }
}