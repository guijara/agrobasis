package com.agrobasis.core_service.organization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {

    @Mock // Cria um repositório "de mentira" para o teste
    private OrganizationRepository organizationRepository;

    @InjectMocks // Injeta o repositório falso dentro do nosso Service
    private OrganizationService organizationService;

    @Test
    void shouldCreateOrganizationSuccessfully() {
        // Arrange
        OrganizationCreateRequest dto = new OrganizationCreateRequest("Fazenda Nova", "11.222.333/0001-44", "Lucas do Rio Verde - MT");

        // Ensinamos o repositório falso a dizer "não existe" quando perguntarem o CNPJ
        when(organizationRepository.existsByCnpj(dto.cnpj())).thenReturn(false);

        // Ensinamos o repositório a devolver a entidade salva
        Organization savedEntity = new Organization();
        savedEntity.setName(dto.name());
        savedEntity.setCnpj(dto.cnpj());
        when(organizationRepository.save(any(Organization.class))).thenReturn(savedEntity);

        // Act
        OrganizationResponseDto result = organizationService.createOrganization(dto);

        // Assert
        assertThat(result.name()).isEqualTo("Fazenda Nova");
        verify(organizationRepository, times(1)).save(any(Organization.class));
    }

    @Test
    void shouldThrowExceptionWhenCnpjAlreadyExists() {
        // Arrange (Preparação)
        OrganizationCreateRequest dto = new OrganizationCreateRequest("Fazenda Duplicada", "11.222.333/0001-44", "Mutum - MT");

        // Ensinamos o repositório a dizer "SIM, já existe"
        when(organizationRepository.existsByCnpj(dto.cnpj())).thenReturn(true);

        // Act & Assert (Ação e Verificação juntas esperando a falha)
        assertThatThrownBy(() -> organizationService.createOrganization(dto))
                .isInstanceOf(OrganizationAlreadyExistsException.class)
                .hasMessage("Organização com CNPJ 11.222.333/0001-44 já existe.");

        // Garante que o sistema NUNCA chamou o método save() do banco
        verify(organizationRepository, never()).save(any(Organization.class));
    }
}
