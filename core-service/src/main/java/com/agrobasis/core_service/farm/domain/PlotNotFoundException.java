package com.agrobasis.core_service.farm.domain;

public class PlotNotFoundException extends RuntimeException {
    public PlotNotFoundException(String message) {
        super(message);
    }
}
