package com.agrobasis.core_service.organization;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrganizationController.class)
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrganizationService organizationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should create organization and return 201")
    void shouldCreateOrganizationAndReturn201() throws Exception {
        // Arrange
        String name = "AgroTech";
        String cnpj = "00.000.000/0000-00";
        String location = "Cuiabá";
        UUID generatedId = UUID.randomUUID();

        OrganizationCreateRequest request = new OrganizationCreateRequest(name, cnpj, location);
        OrganizationResponseDto response = new OrganizationResponseDto(generatedId, name, cnpj, location, LocalDateTime.now());

        given(organizationService.createOrganization(any(OrganizationCreateRequest.class)))
                .willReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(generatedId.toString()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.cnpj").value(cnpj));
    }

    @Test
    @DisplayName("Should return 400 when request body is invalid")
    void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 500 when service throws unexpected exception")
    void shouldReturn500WhenServiceThrowsException() throws Exception {
        // Arrange: Payload obrigatoriamente válido para passar no @Valid
        OrganizationCreateRequest request = new OrganizationCreateRequest(
                "AgroTech Falha",
                "00.000.000/0000-00",
                "Cuiabá"
        );

        given(organizationService.createOrganization(any(OrganizationCreateRequest.class)))
                .willThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        mockMvc.perform(post("/api/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}