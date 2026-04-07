package com.agrobasis.core_service.market.application;

import com.agrobasis.core_service.market.api.dto.ExchangeRateCreateRequest;
import com.agrobasis.core_service.market.api.dto.ExchangeRateResponse;
import com.agrobasis.core_service.market.api.dto.ExchangeRateUpdateRequest;
import com.agrobasis.core_service.market.domain.ExchangeRate;
import com.agrobasis.core_service.market.domain.exception.ExchangeRateNotFoundException;
import com.agrobasis.core_service.market.infrastructure.ExchangeRateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateResponse createExchangeRate(ExchangeRateCreateRequest request) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setFromCurrency(request.fromCurrency());
        exchangeRate.setToCurrency(request.toCurrency());
        exchangeRate.setRate(request.rate());
        exchangeRate.setSource(request.source());
        exchangeRate.setQuotedAt(request.quotedAt());

        ExchangeRate savedExchangeRate = exchangeRateRepository.save(exchangeRate);
        return toResponse(savedExchangeRate);
    }

    public ExchangeRateResponse getExchangeRateById(UUID id) {
        ExchangeRate exchangeRate = exchangeRateRepository.findById(id)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Taxa de câmbio não encontrada."));

        return toResponse(exchangeRate);
    }

    public Page<ExchangeRateResponse> getAllExchangeRates(Pageable pageable) {
        return exchangeRateRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public ExchangeRateResponse updateExchangeRate(UUID id, ExchangeRateUpdateRequest request) {
        ExchangeRate exchangeRate = exchangeRateRepository.findById(id)
                .orElseThrow(() -> new ExchangeRateNotFoundException("Taxa de câmbio não encontrada."));

        exchangeRate.setFromCurrency(request.fromCurrency());
        exchangeRate.setToCurrency(request.toCurrency());
        exchangeRate.setRate(request.rate());
        exchangeRate.setSource(request.source());
        exchangeRate.setQuotedAt(request.quotedAt());

        exchangeRateRepository.save(exchangeRate);
        return toResponse(exchangeRate);
    }

    private ExchangeRateResponse toResponse(ExchangeRate exchangeRate) {
        return new ExchangeRateResponse(
                exchangeRate.getId(),
                exchangeRate.getFromCurrency(),
                exchangeRate.getToCurrency(),
                exchangeRate.getRate(),
                exchangeRate.getSource(),
                exchangeRate.getQuotedAt(),
                exchangeRate.getCreatedAt()
        );
    }
}
