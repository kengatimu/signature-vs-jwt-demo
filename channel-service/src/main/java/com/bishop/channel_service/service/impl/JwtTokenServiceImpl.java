package com.bishop.channel_service.service.impl;

import com.bishop.channel_service.dto.ChannelRequestDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.service.JwtTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private static final long EXPIRATION_MILLIS = 5 * 60 * 1000; // 5 minutes

    private final Key hmacKey;

    public JwtTokenServiceImpl(@Value("${jwt.secret}") String base64Secret) {
        // Decode Base64 secret string into a raw byte array key
        byte[] decodedKey = Base64.getDecoder().decode(base64Secret.getBytes(StandardCharsets.UTF_8));
        this.hmacKey = Keys.hmacShaKeyFor(decodedKey);
    }

    @Override
    public String generateToken(ChannelRequestDto dto) {
        return Jwts.builder()
                .setSubject("channel-service")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MILLIS))
                .claim("rrn", dto.getRrn())
                .claim("amount", dto.getAmount())
                .claim("currency", dto.getCurrency())
                .claim("senderName", dto.getSenderName())
                .claim("receiverName", dto.getReceiverName())
                .claim("transactionType", TransactionType.CREDIT_TRANSFER.name())
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
