package com.bishop.signature_middleware_service.service;

import com.bishop.signature_middleware_service.entity.TransactionDetails;
import com.bishop.signature_middleware_service.enums.TransactionType;
import com.bishop.signature_middleware_service.exception.CustomException;

public interface DatabaseService {
    void checkTransactionExists(String rrn, TransactionType type) throws CustomException;

    void saveCreditTransferEntity(String rrn, TransactionDetails entity) throws CustomException;
}
