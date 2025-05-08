package com.bishop.jwt_middleware_service.service;

import com.bishop.jwt_middleware_service.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.Date;

public interface JwtValidationService {
    Jws<Claims> validateToken(String token) throws CustomException;

    boolean isExpired(Date expiration);
}
