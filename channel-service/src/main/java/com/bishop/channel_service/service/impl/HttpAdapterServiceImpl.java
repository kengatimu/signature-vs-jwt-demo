package com.bishop.channel_service.service.impl;

import com.bishop.channel_service.dto.ChannelRequestDto;
import com.bishop.channel_service.dto.ChannelResponseDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.exception.CustomException;
import com.bishop.channel_service.service.HttpAdapterService;
import com.bishop.channel_service.service.HttpResponseProcessorService;
import com.bishop.channel_service.service.JwtTokenService;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.bishop.channel_service.config.ApplicationConstants.HTTP_ERROR;
import static com.bishop.channel_service.config.ApplicationConstants.TIMEOUT_ERROR;

@Service
public class HttpAdapterServiceImpl implements HttpAdapterService {
    private static final Logger log = LoggerFactory.getLogger(HttpAdapterServiceImpl.class);

    private final Gson gson;
    private final String transactionUrl;
    private final String transactionTokenUrl;
    private final CloseableHttpClient closeableHttpClient;
    private final HttpResponseProcessorService httpResponseProcessorService;
    private final JwtTokenService jwtTokenService; // Custom service to generate JWT

    @Autowired
    public HttpAdapterServiceImpl(Gson gson,
                                  @Value("${urls.signature.transaction}") String transactionUrl,
                                  @Value("${urls.jwt.transaction}") String transactionTokenUrl,
                                  @Qualifier("closeableHttpClient2") CloseableHttpClient closeableHttpClient,
                                  HttpResponseProcessorService httpResponseProcessorService,
                                  JwtTokenService jwtTokenService) {
        this.gson = gson;
        this.transactionUrl = transactionUrl;
        this.transactionTokenUrl = transactionTokenUrl;
        this.closeableHttpClient = closeableHttpClient;
        this.httpResponseProcessorService = httpResponseProcessorService;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public ChannelResponseDto sendSignatureHttpTransactionRequest(ChannelRequestDto channelRequestDto, TransactionType type) throws CustomException {
        String rrn = channelRequestDto.getRrn();
        String request = gson.toJson(channelRequestDto);
        log.info("{}: Http Request (Signature): {}", rrn, request);

        try {
            HttpPost httpPost = getPostRequestHeaders(transactionUrl, new StringEntity(request));

            log.info("{}: Sending {} Post HTTP Request Via (Signature): {}", rrn, type, transactionUrl);
            try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
                return httpResponseProcessorService.processTransactionResponse(rrn, response, type);
            }
        } catch (CustomException | IOException e) {
            handleHttpException(rrn, e);
            return null; // Exception will be thrown, therefore unreachable
        }
    }

    @Override
    public ChannelResponseDto sendJwtHttpTransactionRequest(String token, ChannelRequestDto channelRequestDto, TransactionType type) throws CustomException {
        String rrn = channelRequestDto.getRrn();
        String request = gson.toJson(channelRequestDto);
        log.info("{}: Http Request (JWT): {}", rrn, request);

        try {
            // Generate JWT for payload
            String jwt = jwtTokenService.generateToken(channelRequestDto);

            HttpPost httpPost = getPostRequestHeaders(transactionTokenUrl, new StringEntity(request));
            httpPost.setHeader("Authorization", "Bearer " + jwt);

            log.info("{}: Sending {} Post HTTP Request Via (JWT): {}", rrn, type, transactionTokenUrl);
            try (CloseableHttpResponse response = closeableHttpClient.execute(httpPost)) {
                return httpResponseProcessorService.processTransactionResponse(rrn, response, type);
            }
        } catch (CustomException | IOException e) {
            handleHttpException(rrn, e);
            return null; // Exception will be thrown, therefore unreachable
        }
    }

    // Builds a basic HTTP POST request with content-type header
    private HttpPost getPostRequestHeaders(String url, StringEntity stringEntity) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return httpPost;
    }

    // Handles exception logic common to both signature and jwt calls
    private void handleHttpException(String rrn, Exception e) throws CustomException {
        log.error("{}: Exception occurred when sending HTTP request: {}", rrn, e.getMessage());

        String errorDescription = e.getMessage();
        if (errorDescription != null &&
                (errorDescription.contains("timeout")
                        || errorDescription.contains("time out")
                        || errorDescription.contains("timed out"))) {
            throw new CustomException(TIMEOUT_ERROR + e.getMessage());
        }
        throw new CustomException(HTTP_ERROR + e.getMessage());
    }
}
