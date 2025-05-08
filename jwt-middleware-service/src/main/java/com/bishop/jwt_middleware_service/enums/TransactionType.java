package com.bishop.jwt_middleware_service.enums;

public enum TransactionType {
    CREDIT_TRANSFER("Credit Transfer");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
