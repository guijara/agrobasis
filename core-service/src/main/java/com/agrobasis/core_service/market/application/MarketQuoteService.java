package com.agrobasis.core_service.market.application;

import com.agrobasis.core_service.market.api.dto.MarketQuoteCreateRequest;
import com.agrobasis.core_service.market.api.dto.MarketQuoteResponse;
import com.agrobasis.core_service.market.api.dto.MarketQuoteUpdateRequest;
import com.agrobasis.core_service.market.domain.MarketQuote;
import com.agrobasis.core_service.market.domain.exception.MarketQuoteNotFoundException;
import com.agrobasis.core_service.market.infrastructure.MarketQuoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarketQuoteService {

    private final MarketQuoteRepository marketQuoteRepository;

    public MarketQuoteResponse createMarketQuote(MarketQuoteCreateRequest request) {
        MarketQuote marketQuote = new MarketQuote();
        marketQuote.setCommodity(request.commodity());
        marketQuote.setSource(request.source());
        marketQuote.setPrice(request.price());
        marketQuote.setCurrency(request.currency());
        marketQuote.setUnit(request.unit());
        marketQuote.setQuotedAt(request.quotedAt());

        MarketQuote savedMarketQuote = marketQuoteRepository.save(marketQuote);
        return toResponse(savedMarketQuote);
    }

    public MarketQuoteResponse getMarketQuoteById(UUID id) {
        MarketQuote marketQuote = marketQuoteRepository.findById(id)
                .orElseThrow(() -> new MarketQuoteNotFoundException("Cotação de mercado não encontrada."));

        return toResponse(marketQuote);
    }

    public Page<MarketQuoteResponse> getAllMarketQuotes(Pageable pageable) {
        return marketQuoteRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public MarketQuoteResponse updateMarketQuote(UUID id, MarketQuoteUpdateRequest request) {
        MarketQuote marketQuote = marketQuoteRepository.findById(id)
                .orElseThrow(() -> new MarketQuoteNotFoundException("Cotação de mercado não encontrada."));

        marketQuote.setCommodity(request.commodity());
        marketQuote.setSource(request.source());
        marketQuote.setPrice(request.price());
        marketQuote.setCurrency(request.currency());
        marketQuote.setUnit(request.unit());
        marketQuote.setQuotedAt(request.quotedAt());

        marketQuoteRepository.save(marketQuote);
        return toResponse(marketQuote);
    }

    private MarketQuoteResponse toResponse(MarketQuote marketQuote) {
        return new MarketQuoteResponse(
                marketQuote.getId(),
                marketQuote.getCommodity(),
                marketQuote.getSource(),
                marketQuote.getPrice(),
                marketQuote.getCurrency(),
                marketQuote.getUnit(),
                marketQuote.getQuotedAt(),
                marketQuote.getCreatedAt()
        );
    }
}
