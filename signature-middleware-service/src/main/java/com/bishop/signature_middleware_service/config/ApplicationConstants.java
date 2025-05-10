package com.bishop.signature_middleware_service.config;

public final class ApplicationConstants {

    // Constants
    public static final String CHANNEL_ID = "OMNI";
    public static final int DEFAULT_HTTP_STATUS_CODE = 400;

    // Application errors
    public static final String CERT_VERIFICATION_ERROR = "422|Certificate Verification Error: ";
    public static final String FIELD_VALIDATION_ERROR = "400|Internal Error: Field Validation Failed. ";
    public static final String DEFAULT_PROCESSING_FAILURE = "400|Internal Error: Could not process the request. ";
    public static final String DUPLICATE_RECORD = "409|De-Dup! The request is a duplicate and has already been processed.";
    public static final String DEFAULT_DATABASE_ERROR = "400|Internal Database Error: Error occurred while saving transaction: ";

    private ApplicationConstants() {
    }
}