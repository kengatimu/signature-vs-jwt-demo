package com.bishop.signature_middleware_service.service;

import com.bishop.signature_middleware_service.dto.TransactionRequestDto;
import com.bishop.signature_middleware_service.dto.TransactionResponseDto;
import com.bishop.signature_middleware_service.entity.TransactionDetails;
import com.bishop.signature_middleware_service.enums.TransactionType;

public interface TransactionMapperService {
    TransactionDetails mapRequestToEntity(TransactionRequestDto request, TransactionType type);

    TransactionResponseDto composeChannelResponse(TransactionRequestDto requestDto);
}
