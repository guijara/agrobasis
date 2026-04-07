package com.agrobasis.core_service.market.infrastructure;

import com.agrobasis.core_service.market.domain.ExchangeRate;
import com.agrobasis.core_service.market.domain.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, UUID> {
    Page<ExchangeRate> findAllByFromCurrencyAndToCurrency(Currency fromCurrency, Currency toCurrency, Pageable pageable);

    Optional<ExchangeRate> findTopByFromCurrencyAndToCurrencyOrderByQuotedAtDesc(Currency fromCurrency, Currency toCurrency);
}
