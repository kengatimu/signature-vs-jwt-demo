package com.bishop.channel_service.service;

import com.bishop.channel_service.dto.ChannelRequestDto;
import com.bishop.channel_service.dto.ChannelResponseDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.exception.CustomException;
import org.springframework.validation.BindingResult;

public interface RequestProcessorService {
    ChannelResponseDto processSignatureTransactionRequest(String rrn, ChannelRequestDto channelRequestDto, BindingResult bindingResult, TransactionType type) throws CustomException;

    ChannelResponseDto processJwtTransactionRequest(String rrn, ChannelRequestDto channelRequestDto, BindingResult bindingResult, TransactionType type) throws CustomException;
}
