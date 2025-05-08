package com.bishop.channel_service.web.controller;

import com.bishop.channel_service.dto.ChannelRequestDto;
import com.bishop.channel_service.dto.ChannelResponseDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.exception.CustomException;
import com.bishop.channel_service.service.RequestProcessorService;
import com.google.gson.Gson;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/api/v1")
public class SignatureController {
    private static final Logger log = LoggerFactory.getLogger(SignatureController.class);

    private final Gson gson;
    private final RequestProcessorService requestProcessorService;

    @Autowired
    public SignatureController(Gson gson,
                               RequestProcessorService requestProcessorService) {
        this.gson = gson;
        this.requestProcessorService = requestProcessorService;
    }

    @PostMapping(path = "/signature/transaction",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processTransaction(@Valid @RequestBody ChannelRequestDto requestDto, BindingResult bindingResult, WebRequest webRequest) throws CustomException {
        final TransactionType type = TransactionType.CREDIT_TRANSFER;
        final String rrn = requestDto.getRrn();

        // Store request details in WebRequest for error tracing
        webRequest.setAttribute("rrn", rrn, WebRequest.SCOPE_REQUEST);
        webRequest.setAttribute("type", type, WebRequest.SCOPE_REQUEST);

        try {
            // Log incoming request
            log.info("{}: Received {} Request From Channel: {}", rrn, type, gson.toJson(requestDto));

            // Process channel request
            ChannelResponseDto response = requestProcessorService.processSignatureTransactionRequest(rrn, requestDto, bindingResult, type);

            // Log outgoing response
            log.info("{}: Returned {} Response To Channel: {}", rrn, type, gson.toJson(response));

            return ResponseEntity.ok(gson.toJson(response));
        } catch (Exception e) {
            log.error("{}: Exception Occurred During processing: {}", rrn, e.getMessage());
            throw new CustomException(e.getMessage());
        }
    }
}
