package com.agrobasis.core_service.crop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CropUseCaseTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Deve realizar o ciclo de criar uma safra para um talhão")
    void shouldCreateCropSuccessfully() {
        // Arrange
        UUID plotId = UUID.randomUUID();
        CropRequestDto request = new CropRequestDto(
                "Safra Verão 2026",
                "SOJA",
                LocalDate.now(),
                LocalDate.now().plusMonths(4),
                plotId
        );

        // Act & Assert
        webTestClient.post()
                .uri("/api/crop")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CropResponseDto.class)
                .value(response -> {
                    assertThat(response.product()).isEqualTo("SOJA");
                    assertThat(response.id()).isNotNull();
                });
    }
}
