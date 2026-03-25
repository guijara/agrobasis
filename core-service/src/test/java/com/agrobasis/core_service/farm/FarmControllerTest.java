package com.agrobasis.core_service.farm;

import com.agrobasis.core_service.organization.OrganizationNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@WebMvcTest(FarmController.class)
class FarmControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private FarmService farmService;

    @Test
    @DisplayName("Should create farm and return 201")
    void shouldCreateFarmAndReturn201() {
        // Arrange
        UUID orgId = UUID.randomUUID();
        UUID farmId = UUID.randomUUID();

        FarmRequestDto request = new FarmRequestDto("Fazenda", "Cuiabá", 1500.50, orgId);
        FarmResponseDto response = new FarmResponseDto(farmId, "Fazenda", "Cuiabá", 1500.50, orgId);

        given(farmService.createFarm(any(FarmRequestDto.class))).willReturn(response);

        // Act & Assert
        assertThat(
                mockMvc.post().uri("/api/farm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .hasPathSatisfying("$.id", v -> assertThat(v).isEqualTo(farmId.toString()))
                .hasPathSatisfying("$.name", v -> assertThat(v).isEqualTo("Fazenda"))
                .hasPathSatisfying("$.location", v -> assertThat(v).isEqualTo("Cuiabá"))
                .hasPathSatisfying("$.hectareArea", v -> assertThat(v).isEqualTo(1500.50))
                .hasPathSatisfying("$.organizationId", v -> assertThat(v).isEqualTo(orgId.toString()));
    }

    @Test
    @DisplayName("Should return 404 when organization is not found")
    void shouldReturn404WhenOrganizationIsNotFound() {
        // Arrange
        UUID invalidOrgId = UUID.randomUUID();
        FarmRequestDto request = new FarmRequestDto("Fazenda", "Cuiabá", 1500.50, invalidOrgId);

        given(farmService.createFarm(any(FarmRequestDto.class)))
                .willThrow(new OrganizationNotFoundException("Organização não encontrada."));

        // Act & Assert
        assertThat(
                mockMvc.post().uri("/api/farm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request))
        )
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 400 when request body is invalid")
    void shouldReturn400WhenRequestBodyIsInvalid() {
        // Arrange
        FarmRequestDto invalidRequest = new FarmRequestDto("", "", -10.0, null);

        // Act & Assert
        assertThat(
                mockMvc.post().uri("/api/farm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidRequest))
        )
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should return 200 and FarmResponseDto when valid ID is provided")
    void shouldReturn200WhenFarmIsFound() {
        // Arrange
        UUID farmId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        FarmResponseDto response = new FarmResponseDto(farmId, "Fazenda", "Cuiabá", 1500.50, orgId);

        given(farmService.getFarmById(farmId)).willReturn(response);

        // Act & Assert
        assertThat(
                mockMvc.get().uri("/api/farm/{id}", farmId)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                .hasPathSatisfying("$.name", v -> assertThat(v).isEqualTo("Fazenda"));
    }

    @Test
    @DisplayName("Should return 404 when farm ID does not exist")
    void shouldReturn404WhenFarmIsNotFound() {
        // Arrange
        UUID invalidId = UUID.randomUUID();

        given(farmService.getFarmById(invalidId))
                .willThrow(new FarmNotFoundException("Fazenda não encontrada."));

        // Act & Assert
        assertThat(
                mockMvc.get().uri("/api/farm/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 200 and a page of farms by organization")
    void shouldReturn200AndPageOfFarms() {
        // Arrange
        UUID orgId = UUID.randomUUID();
        FarmResponseDto dto = new FarmResponseDto(UUID.randomUUID(), "Fazenda Santa Cruz", "Sinop", 2000.0, orgId);

        Page<FarmResponseDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        given(farmService.getAllFarmsByOrganization(eq(orgId), any(Pageable.class))).willReturn(page);

        // Act & Assert
        assertThat(
                mockMvc.get().uri("/api/farm")
                        .param("organizationId", orgId.toString()) // Passando o filtro na URL
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .hasStatus(HttpStatus.OK)
                .bodyJson()
                // Acessa o array "content" que o Spring Page gera no JSON
                .hasPathSatisfying("$.content[0].name", v -> assertThat(v).isEqualTo("Fazenda Santa Cruz"))
                .hasPathSatisfying("$.totalElements", v -> assertThat(v).isEqualTo(1));
    }
}