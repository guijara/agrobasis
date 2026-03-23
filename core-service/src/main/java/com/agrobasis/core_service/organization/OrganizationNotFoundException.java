package com.agrobasis.core_service.organization;

public class OrganizationNotFoundException extends RuntimeException {
    public OrganizationNotFoundException(String message) {
        super(message);
    }
}
