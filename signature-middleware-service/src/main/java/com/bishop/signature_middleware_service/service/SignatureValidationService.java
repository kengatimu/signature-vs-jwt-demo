package com.bishop.signature_middleware_service.service;

import com.bishop.signature_middleware_service.dto.TransactionRequestDto;
import com.bishop.signature_middleware_service.enums.TransactionType;
import com.bishop.signature_middleware_service.exception.CustomException;

public interface SignatureValidationService {
    void validateSignature(TransactionRequestDto transactionRequestDto, TransactionType type) throws CustomException;
}
