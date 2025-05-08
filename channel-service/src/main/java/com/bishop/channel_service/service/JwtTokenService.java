package com.bishop.channel_service.service;

import com.bishop.channel_service.dto.ChannelRequestDto;

public interface JwtTokenService {
    String generateToken(ChannelRequestDto dto);
}
