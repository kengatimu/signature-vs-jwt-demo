package com.bishop.channel_service.service;

import com.bishop.channel_service.dto.ChannelResponseDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.exception.CustomException;
import org.apache.http.client.methods.CloseableHttpResponse;

public interface HttpResponseProcessorService {
    ChannelResponseDto processTransactionResponse(String rrn, CloseableHttpResponse response, TransactionType type) throws CustomException;
}
