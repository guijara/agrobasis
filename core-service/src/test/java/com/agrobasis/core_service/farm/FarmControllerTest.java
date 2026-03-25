package com.agrobasis.core_service.farm;

import com.agrobasis.core_service.organization.OrganizationNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
}