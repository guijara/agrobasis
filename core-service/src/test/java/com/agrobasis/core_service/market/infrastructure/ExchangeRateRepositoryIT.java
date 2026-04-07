package com.agrobasis.core_service.market.infrastructure;

import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.ExchangeRate;
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
class ExchangeRateRepositoryIT {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Test
    @DisplayName("Should save exchange rate successfully")
    void shouldSaveExchangeRateSuccessfully() {
        ExchangeRate exchangeRate = createExchangeRate(
                Currency.USD,
                Currency.BRL,
                "5.421300",
                "Banco Central",
                LocalDateTime.of(2026, 4, 7, 10, 0)
        );

        ExchangeRate savedExchangeRate = exchangeRateRepository.save(exchangeRate);

        assertThat(savedExchangeRate.getId()).isNotNull();
        assertThat(savedExchangeRate.getFromCurrency()).isEqualTo(Currency.USD);
        assertThat(savedExchangeRate.getToCurrency()).isEqualTo(Currency.BRL);
        assertThat(savedExchangeRate.getRate()).isEqualByComparingTo("5.421300");
    }

    @Test
    @DisplayName("Should find exchange rates by currency pair with pagination")
    void shouldFindExchangeRatesByCurrencyPairWithPagination() {
        exchangeRateRepository.save(createExchangeRate(Currency.USD, Currency.BRL, "5.421300", "Banco Central", LocalDateTime.of(2026, 4, 7, 10, 0)));
        exchangeRateRepository.save(createExchangeRate(Currency.USD, Currency.BRL, "5.389100", "AwesomeAPI", LocalDateTime.of(2026, 4, 7, 11, 0)));
        exchangeRateRepository.save(createExchangeRate(Currency.BRL, Currency.USD, "0.185000", "Banco Central", LocalDateTime.of(2026, 4, 7, 12, 0)));

        Pageable pageable = PageRequest.of(0, 10);

        Page<ExchangeRate> result = exchangeRateRepository.findAllByFromCurrencyAndToCurrency(Currency.USD, Currency.BRL, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(ExchangeRate::getFromCurrency)
                .containsOnly(Currency.USD);
        assertThat(result.getContent())
                .extracting(ExchangeRate::getToCurrency)
                .containsOnly(Currency.BRL);
    }

    @Test
    @DisplayName("Should find latest exchange rate by currency pair")
    void shouldFindLatestExchangeRateByCurrencyPair() {
        exchangeRateRepository.save(createExchangeRate(Currency.USD, Currency.BRL, "5.421300", "Banco Central", LocalDateTime.of(2026, 4, 7, 10, 0)));
        exchangeRateRepository.save(createExchangeRate(Currency.USD, Currency.BRL, "5.389100", "AwesomeAPI", LocalDateTime.of(2026, 4, 7, 11, 0)));
        exchangeRateRepository.save(createExchangeRate(Currency.BRL, Currency.USD, "0.185000", "Banco Central", LocalDateTime.of(2026, 4, 7, 12, 0)));

        ExchangeRate result = exchangeRateRepository
                .findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency.USD, Currency.BRL)
                .orElseThrow();

        assertThat(result.getFromCurrency()).isEqualTo(Currency.USD);
        assertThat(result.getToCurrency()).isEqualTo(Currency.BRL);
        assertThat(result.getSource()).isEqualTo("AwesomeAPI");
        assertThat(result.getQuotedAt()).isEqualTo(LocalDateTime.of(2026, 4, 7, 11, 0));
    }

    private ExchangeRate createExchangeRate(
            Currency fromCurrency,
            Currency toCurrency,
            String rate,
            String source,
            LocalDateTime quotedAt
    ) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setFromCurrency(fromCurrency);
        exchangeRate.setToCurrency(toCurrency);
        exchangeRate.setRate(new BigDecimal(rate));
        exchangeRate.setSource(source);
        exchangeRate.setQuotedAt(quotedAt);
        return exchangeRate;
    }
}
