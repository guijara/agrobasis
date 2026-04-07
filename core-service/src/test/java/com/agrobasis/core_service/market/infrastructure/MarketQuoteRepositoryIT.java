package com.agrobasis.core_service.market.infrastructure;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.MarketQuote;
import com.agrobasis.core_service.market.domain.Unit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MarketQuoteRepositoryIT {

    @Autowired
    private MarketQuoteRepository marketQuoteRepository;

    @Test
    @DisplayName("Should save market quote successfully")
    void shouldSaveMarketQuoteSuccessfully() {
        MarketQuote marketQuote = createMarketQuote(
                Commodity.SOYBEAN,
                "CEPEA",
                "132.45",
                Currency.USD,
                Unit.TON,
                LocalDateTime.of(2026, 4, 7, 10, 0)
        );

        MarketQuote savedMarketQuote = marketQuoteRepository.save(marketQuote);

        assertThat(savedMarketQuote.getId()).isNotNull();
        assertThat(savedMarketQuote.getCommodity()).isEqualTo(Commodity.SOYBEAN);
        assertThat(savedMarketQuote.getSource()).isEqualTo("CEPEA");
        assertThat(savedMarketQuote.getPrice()).isEqualByComparingTo("132.45");
    }

    @Test
    @DisplayName("Should find market quotes by commodity with pagination")
    void shouldFindMarketQuotesByCommodityWithPagination() {
        marketQuoteRepository.save(createMarketQuote(Commodity.SOYBEAN, "CEPEA", "132.45", Currency.USD, Unit.TON, LocalDateTime.of(2026, 4, 7, 10, 0)));
        marketQuoteRepository.save(createMarketQuote(Commodity.SOYBEAN, "B3", "131.10", Currency.BRL, Unit.TON, LocalDateTime.of(2026, 4, 7, 11, 0)));
        marketQuoteRepository.save(createMarketQuote(Commodity.CORN, "CEPEA", "98.50", Currency.BRL, Unit.TON, LocalDateTime.of(2026, 4, 7, 12, 0)));

        Pageable pageable = PageRequest.of(0, 10);

        Page<MarketQuote> result = marketQuoteRepository.findAllByCommodity(Commodity.SOYBEAN, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(MarketQuote::getCommodity)
                .containsOnly(Commodity.SOYBEAN);
    }

    private MarketQuote createMarketQuote(
            Commodity commodity,
            String source,
            String price,
            Currency currency,
            Unit unit,
            LocalDateTime quotedAt
    ) {
        MarketQuote marketQuote = new MarketQuote();
        marketQuote.setCommodity(commodity);
        marketQuote.setSource(source);
        marketQuote.setPrice(new BigDecimal(price));
        marketQuote.setCurrency(currency);
        marketQuote.setUnit(unit);
        marketQuote.setQuotedAt(quotedAt);
        return marketQuote;
    }
}
