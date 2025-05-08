package com.bishop.channel_service.service.impl;

import com.bishop.channel_service.dto.ChannelResponseDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.exception.CustomException;
import com.bishop.channel_service.service.HttpResponseProcessorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.bishop.channel_service.config.ApplicationConstants.*;

@Service
public class HttpResponseProcessorServiceImpl implements HttpResponseProcessorService {
    private static final Logger log = LoggerFactory.getLogger(HttpResponseProcessorServiceImpl.class);

    private final ObjectMapper objectMapper;

    public HttpResponseProcessorServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ChannelResponseDto processTransactionResponse(String rrn, CloseableHttpResponse response, TransactionType type) throws CustomException {
        try {
            // Extract HTTP entity and status details
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            String httpStatusMsg = response.getStatusLine().getReasonPhrase();

            // Validate if response entity is empty or status is invalid
            if (entity == null || entity.getContentLength() == 0 || statusCode == 0) {
                throw new CustomException(TIMEOUT_ERROR);
            }

            // Convert response entity to String
            String responseString = EntityUtils.toString(entity, "UTF-8");

            // Log the raw HTTP response
            log.info(String.format(HTTP_RESPONSE_LOG_TEMPLATE, statusCode, httpStatusMsg, responseString));

            // Map response JSON to TransactionResponse object
            return composeResponseObject(rrn, responseString);

        } catch (CustomException | IOException e) {
            throw new CustomException(DEFAULT_PROCESSING_FAILURE + e.getMessage());
        }
    }

    private ChannelResponseDto composeResponseObject(String rrn, String responseString) throws CustomException {
        try {
            JsonNode root = objectMapper.readTree(responseString);

            ChannelResponseDto responseDto = new ChannelResponseDto();
            responseDto.setRrn(root.path("rrn").asText(null));
            responseDto.setStatus(root.path("status").asText(null));
            responseDto.setResponseCode(root.path("responseCode").asText(null));
            responseDto.setResponseDesc(root.path("responseDesc").asText(null));

            return responseDto;

        } catch (Exception e) {
            log.error("{}: JSON response parsing error: {}", rrn, e.getMessage());
            throw new CustomException(DEFAULT_RESPONSE_PROCESSING_FAILURE + e.getMessage());
        }
    }
}
