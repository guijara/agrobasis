package com.agrobasis.core_service.organization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@WebMvcTest(OrganizationController.class)
class OrganizationControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private OrganizationService organizationService;

    @Nested
    @DisplayName("POST /api/organization")
    class CreateOrganizationTests {

        @Test
        @DisplayName("Should create organization and return 201")
        void shouldCreateOrganizationAndReturn201() {
            // Arrange
            String name = "AgroTech";
            String cnpj = "00.000.000/0000-00";
            String location = "Cuiabá";
            UUID generatedId = UUID.randomUUID();

            OrganizationRequestDto request = new OrganizationRequestDto(name, cnpj, location);
            OrganizationResponseDto response = new OrganizationResponseDto(
                    generatedId, name, cnpj, location, LocalDateTime.now()
            );

            given(organizationService.createOrganization(any(OrganizationRequestDto.class)))
                    .willReturn(response);

            // Act & Assert
            assertThat(
                    mockMvc.post().uri("/api/organization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(request))
            )
                    .hasStatus(HttpStatus.CREATED)
                    .bodyJson()
                    .hasPathSatisfying("$.id", v -> assertThat(v).isEqualTo(generatedId.toString()))
                    .hasPathSatisfying("$.name", v -> assertThat(v).isEqualTo(name))
                    .hasPathSatisfying("$.cnpj", v -> assertThat(v).isEqualTo(cnpj));
        }

        @Test
        @DisplayName("Should return 400 when request body is invalid")
        void shouldReturn400WhenRequestBodyIsInvalid() {
            // Arrange
            OrganizationRequestDto invalidRequest = new OrganizationRequestDto("", "", "");

            // Act & Assert
            assertThat(
                    mockMvc.post().uri("/api/organization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(invalidRequest))
            )
                    .hasStatus(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("Should return 500 when service throws unexpected exception")
        void shouldReturn500WhenServiceThrowsException() {
            // Arrange
            OrganizationRequestDto request = new OrganizationRequestDto("AgroTech", "00.000.000/0000-00", "Cuiabá");

            given(organizationService.createOrganization(any(OrganizationRequestDto.class)))
                    .willThrow(new RuntimeException("Unexpected error"));

            // Act & Assert
            assertThat(
                    mockMvc.post().uri("/api/organization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonMapper.writeValueAsString(request))
            )
                    .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Nested
    @DisplayName("GET /api/organization")
    class GetOrganizationTests {

        @Test
        @DisplayName("Should return 200 and a page of organizations")
        void shouldReturn200AndPageOfOrganizations() {
            // Arrange
            UUID id = UUID.randomUUID();
            OrganizationResponseDto dto = new OrganizationResponseDto(
                    id, "AgroTech", "00.000.000/0000-00", "Cuiabá", LocalDateTime.now()
            );

            Page<OrganizationResponseDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

            given(organizationService.getAllOrganizations(any(Pageable.class))).willReturn(page);

            // Act & Assert
            assertThat(
                    mockMvc.get().uri("/api/organization")
                            .param("page", "0")
                            .param("size", "10")
                            .accept(MediaType.APPLICATION_JSON)
            )
                    .hasStatus(HttpStatus.OK)
                    .bodyJson()
                    .hasPathSatisfying("$.content[0].name", v -> assertThat(v).isEqualTo("AgroTech"))
                    .hasPathSatisfying("$.totalElements", v -> assertThat(v).isEqualTo(1));
        }

        @Test
        @DisplayName("Should return empty page when no organizations exist")
        void shouldReturnEmptyPageWhenNoData() {
            // Arrange
            Page<OrganizationResponseDto> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
            given(organizationService.getAllOrganizations(any(Pageable.class))).willReturn(emptyPage);

            // Act & Assert
            assertThat(mockMvc.get().uri("/api/organization"))
                    .hasStatus(HttpStatus.OK)
                    .bodyJson()
                    .hasPathSatisfying("$.content", v -> assertThat(v).asList().isEmpty())
                    .hasPathSatisfying("$.totalElements", v -> assertThat(v).isEqualTo(0));
        }
    }
}