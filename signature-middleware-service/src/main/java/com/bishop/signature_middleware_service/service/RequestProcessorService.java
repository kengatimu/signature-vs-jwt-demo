package com.bishop.signature_middleware_service.service;

import com.bishop.signature_middleware_service.dto.TransactionRequestDto;
import com.bishop.signature_middleware_service.dto.TransactionResponseDto;
import com.bishop.signature_middleware_service.enums.TransactionType;
import com.bishop.signature_middleware_service.exception.CustomException;
import org.springframework.validation.BindingResult;

public interface RequestProcessorService {
    TransactionResponseDto processTransactionRequestForSignature(String rrn, TransactionRequestDto transactionRequestDto, BindingResult bindingResult, TransactionType type) throws CustomException;
}
