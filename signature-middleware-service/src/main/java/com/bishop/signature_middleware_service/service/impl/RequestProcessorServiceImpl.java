package com.bishop.signature_middleware_service.service.impl;

import com.bishop.signature_middleware_service.dto.TransactionRequestDto;
import com.bishop.signature_middleware_service.dto.TransactionResponseDto;
import com.bishop.signature_middleware_service.enums.TransactionStatus;
import com.bishop.signature_middleware_service.enums.TransactionType;
import com.bishop.signature_middleware_service.exception.CustomException;
import com.bishop.signature_middleware_service.service.RequestProcessorService;
import com.bishop.signature_middleware_service.service.SignatureValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

import static com.bishop.signature_middleware_service.config.ApplicationConstants.FIELD_VALIDATION_ERROR;

@Service
public class RequestProcessorServiceImpl implements RequestProcessorService {
    private static final Logger log = LoggerFactory.getLogger(RequestProcessorServiceImpl.class);

    private final SignatureValidationService signatureValidationService;

    @Autowired
    public RequestProcessorServiceImpl(SignatureValidationService signatureValidationService) {
        this.signatureValidationService = signatureValidationService;
    }

    @Override
    public TransactionResponseDto processTransactionRequestForSignature(String rrn, TransactionRequestDto requestDto, BindingResult bindingResult, TransactionType type) throws CustomException {
        // Validate input fields manually, due to the use of BindingResult
        checkForInputValidationErrors(bindingResult);

        // Generate signature
        signatureValidationService.validateSignature(requestDto, type);

        // Return channel response
        return composeChannelResponse(requestDto);
    }

    private void checkForInputValidationErrors(BindingResult bindingResult) throws CustomException {
        if (!bindingResult.hasErrors()) {
            return;
        }
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        throw new CustomException(FIELD_VALIDATION_ERROR + allErrors.get(0).getDefaultMessage());
    }

    private TransactionResponseDto composeChannelResponse(TransactionRequestDto requestDto) {
        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setRrn(requestDto.getRrn());
        responseDto.setStatus(TransactionStatus.SUCCESS.name());
        responseDto.setResponseCode(TransactionStatus.SUCCESS.getCode());
        responseDto.setResponseDesc(TransactionStatus.SUCCESS.getDescription());
        return responseDto;
    }
}
