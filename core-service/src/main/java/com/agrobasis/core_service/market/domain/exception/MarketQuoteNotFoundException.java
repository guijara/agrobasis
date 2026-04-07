package com.agrobasis.core_service.market.domain.exception;

public class MarketQuoteNotFoundException extends RuntimeException {
    public MarketQuoteNotFoundException(String message) {
        super(message);
    }
}
