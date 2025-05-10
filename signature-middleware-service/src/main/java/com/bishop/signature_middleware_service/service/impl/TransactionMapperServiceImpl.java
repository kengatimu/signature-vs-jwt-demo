package com.bishop.signature_middleware_service.service.impl;

import com.bishop.signature_middleware_service.dto.TransactionRequestDto;
import com.bishop.signature_middleware_service.dto.TransactionResponseDto;
import com.bishop.signature_middleware_service.entity.TransactionDetails;
import com.bishop.signature_middleware_service.enums.TransactionStatus;
import com.bishop.signature_middleware_service.enums.TransactionType;
import com.bishop.signature_middleware_service.service.TransactionMapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionMapperServiceImpl implements TransactionMapperService {
    private static final Logger log = LoggerFactory.getLogger(TransactionMapperServiceImpl.class);

    @Override
    public TransactionDetails mapRequestToEntity(TransactionRequestDto request, TransactionType type) {
        TransactionDetails transaction = new TransactionDetails();
        transaction.setRrn(request.getRrn());
        transaction.setSenderName(request.getSenderName());
        transaction.setReceiverName(request.getReceiverName());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setChannelId(request.getChannelId());
        transaction.setTransactionType(type);
        transaction.setStatus(TransactionStatus.SUCCESS.name());
        transaction.setStatusCode(TransactionStatus.SUCCESS.getCode());
        transaction.setStatusDesc(TransactionStatus.SUCCESS.getDescription());
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        return transaction;
    }

    @Override
    public TransactionResponseDto composeChannelResponse(TransactionRequestDto requestDto) {
        TransactionResponseDto responseDto = new TransactionResponseDto();
        responseDto.setRrn(requestDto.getRrn());
        responseDto.setStatus(TransactionStatus.SUCCESS.name());
        responseDto.setResponseCode(TransactionStatus.SUCCESS.getCode());
        responseDto.setResponseDesc(TransactionStatus.SUCCESS.getDescription());
        return responseDto;
    }
}
