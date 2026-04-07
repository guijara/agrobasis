package com.agrobasis.core_service.market.infrastructure;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.MarketQuote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarketQuoteRepository extends JpaRepository<MarketQuote, UUID> {
    Page<MarketQuote> findAllByCommodity(Commodity commodity, Pageable pageable);

    Optional<MarketQuote> findTopByCommodityOrderByQuotedAtDesc(Commodity commodity);
}
