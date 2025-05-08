package com.bishop.channel_service.service;

import com.bishop.channel_service.dto.ChannelRequestDto;
import com.bishop.channel_service.dto.ChannelResponseDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.exception.CustomException;

public interface HttpAdapterService {
    ChannelResponseDto sendSignatureHttpTransactionRequest(ChannelRequestDto channelRequestDto, TransactionType type) throws CustomException;

    ChannelResponseDto sendJwtHttpTransactionRequest(String token, ChannelRequestDto channelRequestDto, TransactionType type) throws CustomException;
}
