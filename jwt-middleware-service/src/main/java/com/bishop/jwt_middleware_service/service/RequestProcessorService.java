package com.bishop.jwt_middleware_service.service;

import com.bishop.jwt_middleware_service.dto.TransactionRequestDto;
import com.bishop.jwt_middleware_service.dto.TransactionResponseDto;
import com.bishop.jwt_middleware_service.exception.CustomException;
import org.springframework.validation.BindingResult;

public interface RequestProcessorService {
    TransactionResponseDto processTransactionRequestForJwt(String token, TransactionRequestDto transactionRequestDto, BindingResult bindingResult) throws CustomException;
}
