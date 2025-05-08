package com.bishop.channel_service.service.impl;

import com.bishop.channel_service.dto.ChannelRequestDto;
import com.bishop.channel_service.dto.ChannelResponseDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.exception.CustomException;
import com.bishop.channel_service.service.HttpAdapterService;
import com.bishop.channel_service.service.JwtTokenService;
import com.bishop.channel_service.service.RequestProcessorService;
import com.bishop.channel_service.service.SignatureGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

import static com.bishop.channel_service.config.ApplicationConstants.CHANNEL_ID;
import static com.bishop.channel_service.config.ApplicationConstants.FIELD_VALIDATION_ERROR;
import static com.bishop.channel_service.enums.TransactionType.CREDIT_TRANSFER;

@Service
public class RequestProcessorServiceImpl implements RequestProcessorService {
    private static final Logger log = LoggerFactory.getLogger(RequestProcessorServiceImpl.class);

    private final JwtTokenService jwtTokenService;
    private final HttpAdapterService httpAdapterService;
    private final SignatureGenerationService signatureGenerationService;

    @Autowired
    public RequestProcessorServiceImpl(JwtTokenService jwtTokenService,
                                       HttpAdapterService httpAdapterService,
                                       SignatureGenerationService signatureGenerationService) {
        this.jwtTokenService = jwtTokenService;
        this.httpAdapterService = httpAdapterService;
        this.signatureGenerationService = signatureGenerationService;
    }

    @Override
    public ChannelResponseDto processSignatureTransactionRequest(String rrn, ChannelRequestDto requestDto, BindingResult bindingResult, TransactionType type) throws CustomException {
        // Validate request fields from client manually since we are using BindingResult
        checkForInputValidationErrors(bindingResult);

        // Generate digital signature from the selected fields
        String signature = signatureGenerationService.generateSignature(requestDto, type);

        // Enrich the request with channel ID, signature and transaction type
        updateDtoWithSignature(requestDto, signature);

        // Send signed payload to middleware
        return httpAdapterService.sendSignatureHttpTransactionRequest(requestDto, type);
    }

    @Override
    public ChannelResponseDto processJwtTransactionRequest(String rrn, ChannelRequestDto requestDto, BindingResult bindingResult, TransactionType type) throws CustomException {
        // Validate request fields from client manually since we are using BindingResult
        checkForInputValidationErrors(bindingResult);

        // Generate JWT token with embedded claims
        String jwtToken = jwtTokenService.generateToken(requestDto);

        // Enrich the request with channel ID, signature and transaction type
        updateDtoWithSignature(requestDto, null);

        // Send token + payload to middleware
        return httpAdapterService.sendJwtHttpTransactionRequest(jwtToken, requestDto, type);
    }

    private void checkForInputValidationErrors(BindingResult bindingResult) throws CustomException {
        // If validation failed, extract first error and throw
        if (!bindingResult.hasErrors()) return;
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        throw new CustomException(FIELD_VALIDATION_ERROR + allErrors.get(0).getDefaultMessage());
    }

    private void updateDtoWithSignature(ChannelRequestDto requestDto, String signature) {
        requestDto.setSignature(signature);             // Add signature to payload
        requestDto.setChannelId(CHANNEL_ID);            // Add static channel ID (e.g., "OMNI")
        requestDto.setTransactionType(CREDIT_TRANSFER); // Hardcoded for now — could be dynamic in future
    }
}
