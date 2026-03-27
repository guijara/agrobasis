package com.agrobasis.core_service.plot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
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

@WebMvcTest(PlotController.class)
class PlotControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private PlotService plotService;

    @Nested
    @DisplayName("POST /api/plot")
    class CreatePlotTests {
        @Test
        void shouldReturn201OnCreation() throws Exception {
            PlotCreateRequestDto request = new PlotCreateRequestDto("T1", 10.0, UUID.randomUUID());
            PlotResponseDto response = new PlotResponseDto(UUID.randomUUID(), "T1", 10.0, request.farmId());

            given(plotService.createPlot(any())).willReturn(response);

            assertThat(mockMvc.post().uri("/api/plot")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request)))
                    .hasStatus(HttpStatus.CREATED);
        }
    }

    @Nested
    @DisplayName("GET /api/plot/{id}")
    class GetPlotByIdTests {
        @Test
        void shouldReturn200IfFound() {
            UUID id = UUID.randomUUID();
            given(plotService.getPlotById(id)).willReturn(new PlotResponseDto(id, "T1", 10.0, UUID.randomUUID()));

            assertThat(mockMvc.get().uri("/api/plot/{id}", id))
                    .hasStatus(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("GET /api/plot (List)")
    class ListPlotsTests {
        @Test
        void shouldReturn200WithPage() {
            UUID farmId = UUID.randomUUID();
            given(plotService.getAllPlotsByOrganization(eq(farmId), any(Pageable.class)))
                    .willReturn(new PageImpl<>(List.of()));

            // Nota: O teste espera @RequestParam farmId conforme o padrão REST
            assertThat(mockMvc.get().uri("/api/plot").param("farmId", farmId.toString()))
                    .hasStatus(HttpStatus.OK);
        }
    }

    @Nested
    @DisplayName("PUT /api/plot/{id}")
    class UpdatePlotTests {
        @Test
        void shouldReturn200OnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            PlotUpdateRequestDto request = new PlotUpdateRequestDto("Novo", 20.0);
            given(plotService.updatePlot(eq(id), any())).willReturn(new PlotResponseDto(id, "Novo", 20.0, UUID.randomUUID()));

            assertThat(mockMvc.put().uri("/api/plot/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMapper.writeValueAsString(request)))
                    .hasStatus(HttpStatus.OK);
        }
    }
}