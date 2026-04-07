package com.agrobasis.core_service.market.application;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.api.dto.MarketQuoteCreateRequest;
import com.agrobasis.core_service.market.api.dto.MarketQuoteResponse;
import com.agrobasis.core_service.market.api.dto.MarketQuoteUpdateRequest;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.MarketQuote;
import com.agrobasis.core_service.market.domain.Unit;
import com.agrobasis.core_service.market.domain.exception.MarketQuoteNotFoundException;
import com.agrobasis.core_service.market.infrastructure.MarketQuoteRepository;
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
class MarketQuoteServiceTest {

    @Mock
    private MarketQuoteRepository marketQuoteRepository;

    @InjectMocks
    private MarketQuoteService marketQuoteService;

    @Nested
    @DisplayName("createMarketQuote()")
    class CreateMarketQuoteTests {

        @Test
        @DisplayName("Should create market quote successfully")
        void shouldCreateMarketQuoteSuccessfully() {
            LocalDateTime quotedAt = LocalDateTime.of(2026, 4, 7, 10, 0);
            MarketQuoteCreateRequest request = new MarketQuoteCreateRequest(
                    Commodity.SOYBEAN,
                    "CEPEA",
                    new BigDecimal("132.45"),
                    Currency.USD,
                    Unit.TON,
                    quotedAt
            );

            when(marketQuoteRepository.save(any(MarketQuote.class))).thenAnswer(invocation -> invocation.getArgument(0));

            MarketQuoteResponse result = marketQuoteService.createMarketQuote(request);

            assertThat(result.commodity()).isEqualTo(Commodity.SOYBEAN);
            assertThat(result.source()).isEqualTo("CEPEA");
            assertThat(result.price()).isEqualByComparingTo("132.45");
            assertThat(result.currency()).isEqualTo(Currency.USD);
            assertThat(result.unit()).isEqualTo(Unit.TON);
            assertThat(result.quotedAt()).isEqualTo(quotedAt);
            verify(marketQuoteRepository).save(any(MarketQuote.class));
        }
    }

    @Nested
    @DisplayName("getMarketQuoteById()")
    class GetMarketQuoteByIdTests {

        @Test
        @DisplayName("Should return market quote when ID exists")
        void shouldReturnMarketQuoteWhenIdExists() {
            UUID id = UUID.randomUUID();
            LocalDateTime quotedAt = LocalDateTime.of(2026, 4, 7, 11, 0);
            LocalDateTime createdAt = LocalDateTime.of(2026, 4, 7, 11, 5);
            MarketQuote marketQuote = createMarketQuote(id, Commodity.CORN, "B3", "98.10", Currency.BRL, Unit.TON, quotedAt, createdAt);

            when(marketQuoteRepository.findById(id)).thenReturn(Optional.of(marketQuote));

            MarketQuoteResponse result = marketQuoteService.getMarketQuoteById(id);

            assertThat(result.id()).isEqualTo(id);
            assertThat(result.commodity()).isEqualTo(Commodity.CORN);
            assertThat(result.price()).isEqualByComparingTo("98.10");
            assertThat(result.createdAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("Should throw exception when market quote not found")
        void shouldThrowExceptionWhenMarketQuoteNotFound() {
            UUID id = UUID.randomUUID();

            when(marketQuoteRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> marketQuoteService.getMarketQuoteById(id))
                    .isInstanceOf(MarketQuoteNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllMarketQuotes()")
    class GetAllMarketQuotesTests {

        @Test
        @DisplayName("Should return paginated market quotes")
        void shouldReturnPaginatedMarketQuotes() {
            Pageable pageable = PageRequest.of(0, 10);
            MarketQuote marketQuote = createMarketQuote(
                    UUID.randomUUID(),
                    Commodity.SOYBEAN,
                    "CEPEA",
                    "130.00",
                    Currency.USD,
                    Unit.TON,
                    LocalDateTime.of(2026, 4, 7, 9, 0),
                    LocalDateTime.of(2026, 4, 7, 9, 5)
            );

            Page<MarketQuote> page = new PageImpl<>(List.of(marketQuote), pageable, 1);
            when(marketQuoteRepository.findAll(pageable)).thenReturn(page);

            Page<MarketQuoteResponse> result = marketQuoteService.getAllMarketQuotes(pageable);

            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().getFirst().commodity()).isEqualTo(Commodity.SOYBEAN);
            assertThat(result.getContent().getFirst().source()).isEqualTo("CEPEA");
        }
    }

    @Nested
    @DisplayName("updateMarketQuote()")
    class UpdateMarketQuoteTests {

        @Test
        @DisplayName("Should update market quote successfully")
        void shouldUpdateMarketQuoteSuccessfully() {
            UUID id = UUID.randomUUID();
            MarketQuote existingMarketQuote = createMarketQuote(
                    id,
                    Commodity.SOYBEAN,
                    "CEPEA",
                    "120.00",
                    Currency.USD,
                    Unit.TON,
                    LocalDateTime.of(2026, 4, 7, 8, 0),
                    LocalDateTime.of(2026, 4, 7, 8, 5)
            );

            MarketQuoteUpdateRequest request = new MarketQuoteUpdateRequest(
                    Commodity.CORN,
                    "B3",
                    new BigDecimal("99.90"),
                    Currency.BRL,
                    Unit.TON,
                    LocalDateTime.of(2026, 4, 7, 12, 0)
            );

            when(marketQuoteRepository.findById(id)).thenReturn(Optional.of(existingMarketQuote));

            MarketQuoteResponse result = marketQuoteService.updateMarketQuote(id, request);

            assertThat(result.commodity()).isEqualTo(Commodity.CORN);
            assertThat(result.source()).isEqualTo("B3");
            assertThat(result.price()).isEqualByComparingTo("99.90");
            verify(marketQuoteRepository).save(existingMarketQuote);
        }

        @Test
        @DisplayName("Should throw exception when market quote not found on update")
        void shouldThrowExceptionWhenMarketQuoteNotFoundOnUpdate() {
            UUID id = UUID.randomUUID();
            MarketQuoteUpdateRequest request = new MarketQuoteUpdateRequest(
                    Commodity.CORN,
                    "B3",
                    new BigDecimal("99.90"),
                    Currency.BRL,
                    Unit.TON,
                    LocalDateTime.of(2026, 4, 7, 12, 0)
            );

            when(marketQuoteRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> marketQuoteService.updateMarketQuote(id, request))
                    .isInstanceOf(MarketQuoteNotFoundException.class);
        }
    }

    private MarketQuote createMarketQuote(
            UUID id,
            Commodity commodity,
            String source,
            String price,
            Currency currency,
            Unit unit,
            LocalDateTime quotedAt,
            LocalDateTime createdAt
    ) {
        MarketQuote marketQuote = new MarketQuote();
        marketQuote.setId(id);
        marketQuote.setCommodity(commodity);
        marketQuote.setSource(source);
        marketQuote.setPrice(new BigDecimal(price));
        marketQuote.setCurrency(currency);
        marketQuote.setUnit(unit);
        marketQuote.setQuotedAt(quotedAt);
        marketQuote.setCreatedAt(createdAt);
        return marketQuote;
    }
}
