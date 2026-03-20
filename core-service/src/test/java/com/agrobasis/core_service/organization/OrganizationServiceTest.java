package com.agrobasis.core_service.organization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {

    @Mock
    private OrganizationRepository repository;

    @InjectMocks
    private OrganizationService service;

    @Test
    void shouldCreateOrganizationSuccessfully() {
        // Arrange
        OrganizationDTO dto = new OrganizationDTO("Fazenda Nova", "11.222.333/0001-44", "Lucas do Rio Verde - MT");
        when(repository.existsByCnpj(dto.cnpj())).thenReturn(false);

        Organization savedEntity = new Organization();
        savedEntity.setName(dto.name());
        savedEntity.setCnpj(dto.cnpj());
        when(repository.save(any(Organization.class))).thenReturn(savedEntity);

        // Act
        Organization result = service.create(dto);

        // Assert
        assertThat(result.getName()).isEqualTo("Fazenda Nova");
        verify(repository, times(1)).save(any(Organization.class));
    }

    @Test
    void shouldThrowExceptionWhenCnpjAlreadyExists() {
        // Arrange
        OrganizationDTO dto = new OrganizationDTO("Fazenda Duplicada", "11.222.333/0001-44", "Mutum - MT");
        when(repository.existsByCnpj(dto.cnpj())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CNPJ já cadastrado no sistema.");

        verify(repository, never()).save(any(Organization.class));
    }
}
