package com.bishop.channel_service.config;

public final class ApplicationConstants {

    // Constants
    public static final String CHANNEL_ID = "OMNI";
    public static final int DEFAULT_HTTP_STATUS_CODE = 400;

    // Application errors
    public static final String CERT_GENERATION_ERROR = "422|Certificate Generation Error: ";
    public static final String FIELD_VALIDATION_ERROR = "400|Internal Error: Field Validation Failed. ";
    public static final String HTTP_ERROR = "400|Internal Error: HTTP Call To The URL Was Unsuccessful. ";
    public static final String DEFAULT_PROCESSING_FAILURE = "400|Internal Error: Could not process the request. ";
    public static final String TIMEOUT_ERROR = "408|Did not receive a response from remote service, possibly due to timeout. ";
    public static final String DEFAULT_RESPONSE_PROCESSING_FAILURE = "400|Internal Error: Could not process the received response. ";
    public static final String HTTP_RESPONSE_LOG_TEMPLATE = "HTTP Response: \n STATUS CODE: %s\n STATUS MESSAGE: %s\n RESPONSE BODY STRING: %s\n";

    private ApplicationConstants() {
    }
}