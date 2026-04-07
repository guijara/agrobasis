package com.agrobasis.core_service.market.application;

import com.agrobasis.core_service.market.api.dto.ExchangeRateCreateRequest;
import com.agrobasis.core_service.market.api.dto.ExchangeRateResponse;
import com.agrobasis.core_service.market.api.dto.ExchangeRateUpdateRequest;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.ExchangeRate;
import com.agrobasis.core_service.market.domain.exception.ExchangeRateNotFoundException;
import com.agrobasis.core_service.market.infrastructure.ExchangeRateRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Nested
    @DisplayName("createExchangeRate()")
    class CreateExchangeRateTests {

        @Test
        @DisplayName("Should create exchange rate successfully")
        void shouldCreateExchangeRateSuccessfully() {
            LocalDateTime quotedAt = LocalDateTime.of(2026, 4, 7, 10, 0);
            ExchangeRateCreateRequest request = new ExchangeRateCreateRequest(
                    Currency.USD,
                    Currency.BRL,
                    new BigDecimal("5.421300"),
                    "Banco Central",
                    quotedAt
            );

            when(exchangeRateRepository.save(any(ExchangeRate.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ExchangeRateResponse result = exchangeRateService.createExchangeRate(request);

            assertThat(result.fromCurrency()).isEqualTo(Currency.USD);
            assertThat(result.toCurrency()).isEqualTo(Currency.BRL);
            assertThat(result.rate()).isEqualByComparingTo("5.421300");
            assertThat(result.source()).isEqualTo("Banco Central");
            assertThat(result.quotedAt()).isEqualTo(quotedAt);
            verify(exchangeRateRepository).save(any(ExchangeRate.class));
        }
    }

    @Nested
    @DisplayName("getExchangeRateById()")
    class GetExchangeRateByIdTests {

        @Test
        @DisplayName("Should return exchange rate when ID exists")
        void shouldReturnExchangeRateWhenIdExists() {
            UUID id = UUID.randomUUID();
            LocalDateTime quotedAt = LocalDateTime.of(2026, 4, 7, 11, 0);
            LocalDateTime createdAt = LocalDateTime.of(2026, 4, 7, 11, 5);
            ExchangeRate exchangeRate = createExchangeRate(id, Currency.USD, Currency.BRL, "5.389100", "AwesomeAPI", quotedAt, createdAt);

            when(exchangeRateRepository.findById(id)).thenReturn(Optional.of(exchangeRate));

            ExchangeRateResponse result = exchangeRateService.getExchangeRateById(id);

            assertThat(result.id()).isEqualTo(id);
            assertThat(result.fromCurrency()).isEqualTo(Currency.USD);
            assertThat(result.toCurrency()).isEqualTo(Currency.BRL);
            assertThat(result.rate()).isEqualByComparingTo("5.389100");
            assertThat(result.createdAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("Should throw exception when exchange rate not found")
        void shouldThrowExceptionWhenExchangeRateNotFound() {
            UUID id = UUID.randomUUID();

            when(exchangeRateRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> exchangeRateService.getExchangeRateById(id))
                    .isInstanceOf(ExchangeRateNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllExchangeRates()")
    class GetAllExchangeRatesTests {

        @Test
        @DisplayName("Should return paginated exchange rates")
        void shouldReturnPaginatedExchangeRates() {
            Pageable pageable = PageRequest.of(0, 10);
            ExchangeRate exchangeRate = createExchangeRate(
                    UUID.randomUUID(),
                    Currency.USD,
                    Currency.BRL,
                    "5.400000",
                    "Banco Central",
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 7, 9, 5)
            );

            Page<ExchangeRate> page = new PageImpl<>(List.of(exchangeRate), pageable, 1);
            when(exchangeRateRepository.findAll(pageable)).thenReturn(page);

            Page<ExchangeRateResponse> result = exchangeRateService.getAllExchangeRates(pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().getFirst().fromCurrency()).isEqualTo(Currency.USD);
            assertThat(result.getContent().getFirst().toCurrency()).isEqualTo(Currency.BRL);
        }
    }

    @Nested
    @DisplayName("updateExchangeRate()")
    class UpdateExchangeRateTests {

        @Test
        @DisplayName("Should update exchange rate successfully")
        void shouldUpdateExchangeRateSuccessfully() {
            UUID id = UUID.randomUUID();
            ExchangeRate existingExchangeRate = createExchangeRate(
                    id,
                    Currency.USD,
                    Currency.BRL,
                    "5.300000",
                    "Banco Central",
                    LocalDateTime.of(2026, 4, 7, 8, 0),
                    LocalDateTime.of(2026, 4, 7, 8, 5)
            );

            ExchangeRateUpdateRequest request = new ExchangeRateUpdateRequest(
                    Currency.BRL,
                    Currency.USD,
                    new BigDecimal("0.185000"),
                    "AwesomeAPI",
                    LocalDateTime.of(2026, 4, 7, 12, 0)
            );

            when(exchangeRateRepository.findById(id)).thenReturn(Optional.of(existingExchangeRate));

            ExchangeRateResponse result = exchangeRateService.updateExchangeRate(id, request);

            assertThat(result.fromCurrency()).isEqualTo(Currency.BRL);
            assertThat(result.toCurrency()).isEqualTo(Currency.USD);
            assertThat(result.rate()).isEqualByComparingTo("0.185000");
            assertThat(result.source()).isEqualTo("AwesomeAPI");
            verify(exchangeRateRepository).save(existingExchangeRate);
        }

        @Test
        @DisplayName("Should throw exception when exchange rate not found on update")
        void shouldThrowExceptionWhenExchangeRateNotFoundOnUpdate() {
            UUID id = UUID.randomUUID();
            ExchangeRateUpdateRequest request = new ExchangeRateUpdateRequest(
                    Currency.BRL,
                    Currency.USD,
                    new BigDecimal("0.185000"),
                    "AwesomeAPI",
                    LocalDateTime.of(2026, 4, 7, 12, 0)
            );

            when(exchangeRateRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> exchangeRateService.updateExchangeRate(id, request))
                    .isInstanceOf(ExchangeRateNotFoundException.class);
        }
    }

    private ExchangeRate createExchangeRate(
            UUID id,
            Currency fromCurrency,
            Currency toCurrency,
            String rate,
            String source,
            LocalDateTime quotedAt,
            LocalDateTime createdAt
    ) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setId(id);
        exchangeRate.setFromCurrency(fromCurrency);
        exchangeRate.setToCurrency(toCurrency);
        exchangeRate.setRate(new BigDecimal(rate));
        exchangeRate.setSource(source);
        exchangeRate.setQuotedAt(quotedAt);
        exchangeRate.setCreatedAt(createdAt);
        return exchangeRate;
    }
}
