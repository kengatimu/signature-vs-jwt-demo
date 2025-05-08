package com.bishop.jwt_middleware_service.service.impl;

import com.bishop.jwt_middleware_service.exception.CustomException;
import com.bishop.jwt_middleware_service.service.JwtValidationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static com.bishop.jwt_middleware_service.config.ApplicationConstants.JWT_VALIDATION_ERROR;

@Service
public class JwtValidationServiceImpl implements JwtValidationService {
    private static final Logger log = LoggerFactory.getLogger(JwtValidationServiceImpl.class);

    private final Key hmacKey;

    public JwtValidationServiceImpl(@Value("${jwt.secret}") String base64Secret) {
        // Decode the base64-encoded secret into a cryptographic key
        byte[] decodedKey = Base64.getDecoder().decode(base64Secret.getBytes(StandardCharsets.UTF_8));
        this.hmacKey = Keys.hmacShaKeyFor(decodedKey);
    }

    @Override
    public Jws<Claims> validateToken(String token) throws CustomException {
        try {
            // Parse and validate JWT token using the secret HMAC key
            return Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException e) {
            // Token is invalid (expired, malformed, wrong signature, etc.)
            log.error("JwtException Occurred During Jwt validation: {}", e.getMessage());
            throw new CustomException(JWT_VALIDATION_ERROR + "JWT validation failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Token is null or empty
            log.error("IllegalArgumentException Occurred During Jwt validation: {}", e.getMessage());
            throw new CustomException(JWT_VALIDATION_ERROR + "JWT token is missing or empty");
        }
    }

    @Override
    // Check if the token has expired
    public boolean isExpired(Date expiration) {
        return expiration.before(new Date());
    }
}
