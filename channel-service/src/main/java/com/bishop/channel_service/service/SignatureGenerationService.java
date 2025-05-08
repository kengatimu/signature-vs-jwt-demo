package com.bishop.channel_service.service;

import com.bishop.channel_service.dto.ChannelRequestDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.exception.CustomException;

public interface SignatureGenerationService {
    String generateSignature (ChannelRequestDto channelRequestDto, TransactionType type) throws CustomException;
}
