package com.bishop.jwt_middleware_service.service.impl;

import com.bishop.jwt_middleware_service.dto.TransactionRequestDto;
import com.bishop.jwt_middleware_service.dto.TransactionResponseDto;
import com.bishop.jwt_middleware_service.enums.TransactionStatus;
import com.bishop.jwt_middleware_service.exception.CustomException;
import com.bishop.jwt_middleware_service.service.JwtValidationService;
import com.bishop.jwt_middleware_service.service.RequestProcessorService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.math.BigDecimal;
import java.util.List;

import static com.bishop.jwt_middleware_service.config.ApplicationConstants.FIELD_VALIDATION_ERROR;
import static com.bishop.jwt_middleware_service.config.ApplicationConstants.JWT_VALIDATION_ERROR;

@Service
public class RequestProcessorServiceImpl implements RequestProcessorService {
    private static final Logger log = LoggerFactory.getLogger(RequestProcessorServiceImpl.class);

    private final JwtValidationService jwtValidationService;

    @Autowired
    public RequestProcessorServiceImpl(JwtValidationService jwtValidationService) {
        this.jwtValidationService = jwtValidationService;
    }

    @Override
    public TransactionResponseDto processTransactionRequestForJwt(String token, TransactionRequestDto requestDto, BindingResult bindingResult) throws CustomException {
        // Check for field-level validation errors. Checking it manually since we are using BindingResult
        checkForInputValidationErrors(bindingResult);

        // Validate and parse JWT, extract claims
        Jws<Claims> jws = jwtValidationService.validateToken(token);
        Claims claims = jws.getBody();

        // Perform manual validation of claims vs incoming request
        validateClaimsAgainstRequest(claims, requestDto);

        // Return response if validation succeeds
        return composeChannelResponse(requestDto);
    }

    // Ensures request DTO is valid per javax/jakarta annotations
    private void checkForInputValidationErrors(BindingResult bindingResult) throws CustomException {
        if (!bindingResult.hasErrors()) return;
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        throw new CustomException(FIELD_VALIDATION_ERROR + allErrors.get(0).getDefaultMessage());
    }

    // Compare JWT claims with incoming request fields
    private void validateClaimsAgainstRequest(Claims claims, TransactionRequestDto dto) throws CustomException {
        try {
            if (!dto.getRrn().equals(claims.get("rrn", String.class))) {
                throw new CustomException(JWT_VALIDATION_ERROR + "RRN mismatch");
            }

            String tokenTransactionType = claims.get("transactionType", String.class);
            if (!dto.getTransactionType().name().equals(tokenTransactionType)) {
                throw new CustomException(JWT_VALIDATION_ERROR + "Transaction type mismatch");
            }

            if (!dto.getCurrency().equals(claims.get("currency", String.class))) {
                throw new CustomException(JWT_VALIDATION_ERROR + "Currency mismatch");
            }

            if (!dto.getSenderName().equals(claims.get("senderName", String.class))) {
                throw new CustomException(JWT_VALIDATION_ERROR + "Sender name mismatch");
            }

            if (!dto.getReceiverName().equals(claims.get("receiverName", String.class))) {
                throw new CustomException(JWT_VALIDATION_ERROR + "Receiver name mismatch");
            }

            BigDecimal claimAmount = new BigDecimal(claims.get("amount").toString());
            if (dto.getAmount().compareTo(claimAmount) != 0) {
                throw new CustomException(JWT_VALIDATION_ERROR + "Amount mismatch");
            }

        } catch (NullPointerException | IllegalArgumentException e) {
            throw new CustomException(JWT_VALIDATION_ERROR + "Invalid or missing claim values: " + e.getMessage());
        }
    }

    // Builds the success response DTO
    private TransactionResponseDto composeChannelResponse(TransactionRequestDto requestDto) {
        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setRrn(requestDto.getRrn());
        responseDto.setStatus(TransactionStatus.SUCCESS.name());
        responseDto.setResponseCode(TransactionStatus.SUCCESS.getCode());
        responseDto.setResponseDesc(TransactionStatus.SUCCESS.getDescription());
        return responseDto;
    }
}
