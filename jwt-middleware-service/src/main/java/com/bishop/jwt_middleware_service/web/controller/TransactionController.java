package com.bishop.jwt_middleware_service.web.controller;

import com.bishop.jwt_middleware_service.dto.TransactionRequestDto;
import com.bishop.jwt_middleware_service.dto.TransactionResponseDto;
import com.bishop.jwt_middleware_service.enums.TransactionType;
import com.bishop.jwt_middleware_service.exception.CustomException;
import com.bishop.jwt_middleware_service.service.RequestProcessorService;
import com.google.gson.Gson;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/api/v1")
public class TransactionController {
    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final Gson gson;
    private final RequestProcessorService requestProcessorService;

    @Autowired
    public TransactionController(Gson gson,
                                 RequestProcessorService requestProcessorService) {
        this.gson = gson;
        this.requestProcessorService = requestProcessorService;
    }

    @PostMapping(path = "/jwt/transaction",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleJwtTransaction(@Valid @RequestBody TransactionRequestDto requestDto,
                                                  @RequestHeader("Authorization") String authHeader,
                                                  BindingResult bindingResult,
                                                  WebRequest webRequest) throws CustomException {
        String token = authHeader.replace("Bearer ", "");
        final TransactionType type = TransactionType.CREDIT_TRANSFER;
        final String rrn = requestDto.getRrn();

        // Store request details in WebRequest for error tracing
        webRequest.setAttribute("rrn", rrn, WebRequest.SCOPE_REQUEST);
        webRequest.setAttribute("type", type, WebRequest.SCOPE_REQUEST);

        try {
            // Log incoming request
            log.info("{}: Received {} Request From Channel: {}", rrn, type, gson.toJson(requestDto));

            // Process channel request
            TransactionResponseDto response = requestProcessorService.processTransactionRequestForJwt(token, requestDto, bindingResult);

            // Log outgoing response
            log.info("{}: Returned {} Response To Channel: {}", rrn, type, gson.toJson(response));

            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception e) {
            log.error("{}: Exception Occurred During processing: {}", rrn, e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }
}
