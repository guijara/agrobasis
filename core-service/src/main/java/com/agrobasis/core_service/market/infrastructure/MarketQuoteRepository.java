package com.agrobasis.core_service.market.infrastructure;

import com.agrobasis.core_service.market.domain.MarketQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MarketQuoteRepository extends JpaRepository<MarketQuote, UUID> {
}
