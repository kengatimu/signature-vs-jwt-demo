package com.bishop.jwt_middleware_service.config;

public final class ApplicationConstants {

    // Constants
    public static final String CHANNEL_ID = "OMNI";
    public static final int DEFAULT_HTTP_STATUS_CODE = 400;

    // Application errors
    public static final String JWT_VALIDATION_ERROR = "422|JWT Validation Error: ";
    public static final String FIELD_VALIDATION_ERROR = "400|Internal Error: Field Validation Failed. ";
    public static final String DEFAULT_PROCESSING_FAILURE = "400|Internal Error: Could not process the request. ";

    private ApplicationConstants() {
    }
}