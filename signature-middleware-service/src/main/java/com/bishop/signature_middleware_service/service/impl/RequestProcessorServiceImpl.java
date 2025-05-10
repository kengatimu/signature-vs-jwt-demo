package com.bishop.signature_middleware_service.service.impl;

import com.bishop.signature_middleware_service.dto.TransactionRequestDto;
import com.bishop.signature_middleware_service.dto.TransactionResponseDto;
import com.bishop.signature_middleware_service.entity.TransactionDetails;
import com.bishop.signature_middleware_service.enums.TransactionType;
import com.bishop.signature_middleware_service.exception.CustomException;
import com.bishop.signature_middleware_service.service.DatabaseService;
import com.bishop.signature_middleware_service.service.RequestProcessorService;
import com.bishop.signature_middleware_service.service.SignatureValidationService;
import com.bishop.signature_middleware_service.service.TransactionMapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

import static com.bishop.signature_middleware_service.config.ApplicationConstants.FIELD_VALIDATION_ERROR;

@Service
public class RequestProcessorServiceImpl implements RequestProcessorService {
    private static final Logger log = LoggerFactory.getLogger(RequestProcessorServiceImpl.class);

    private final DatabaseService databaseService;
    private final TransactionMapperService transactionMapperService;

    private final SignatureValidationService signatureValidationService;

    @Autowired
    public RequestProcessorServiceImpl(DatabaseService databaseService,
                                       TransactionMapperService transactionMapperService,
                                       SignatureValidationService signatureValidationService) {
        this.databaseService = databaseService;
        this.transactionMapperService = transactionMapperService;
        this.signatureValidationService = signatureValidationService;
    }

    @Override
    public TransactionResponseDto processTransactionRequestForSignature(String rrn, TransactionRequestDto requestDto, BindingResult bindingResult, TransactionType type) throws CustomException {
        // Validate input fields manually, due to the use of BindingResult
        checkForInputValidationErrors(bindingResult);

        // Generate signature
        signatureValidationService.validateSignature(requestDto, type);

        // Check if transaction already exists
        checkTransactionExists(rrn, type);

        // Prepare entity to be persisted
        TransactionDetails entity = transactionMapperService.mapRequestToEntity(requestDto, type);

        // Persist initial transaction record
        persistEntity(entity, rrn);

        // Return channel response
        return transactionMapperService.composeChannelResponse(requestDto);
    }

    private void checkForInputValidationErrors(BindingResult bindingResult) throws CustomException {
        if (!bindingResult.hasErrors()) {
            return;
        }
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        throw new CustomException(FIELD_VALIDATION_ERROR + allErrors.get(0).getDefaultMessage());
    }

    // Use @Transactional(readOnly = true) because we are only reading from the database (no modification)
    @Transactional(readOnly = true)
    public void checkTransactionExists(String rrn, TransactionType type) throws CustomException {
        log.info("{}: Checking transaction by RRN: {} and type: {}", rrn, rrn, type);
        databaseService.checkTransactionExists(rrn, type);
    }

    // Use @Transactional because we are saving a new record to the database
    @Transactional
    public void persistEntity(TransactionDetails entity, String rrn) throws CustomException {
        try {
            log.info("{}: Saving the initial credit transfer record in the database", rrn);
            databaseService.saveCreditTransferEntity(rrn, entity);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

}
