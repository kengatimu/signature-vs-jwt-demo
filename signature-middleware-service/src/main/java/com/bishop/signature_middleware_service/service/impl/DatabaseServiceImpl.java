package com.bishop.signature_middleware_service.service.impl;

import com.bishop.signature_middleware_service.entity.TransactionDetails;
import com.bishop.signature_middleware_service.enums.TransactionType;
import com.bishop.signature_middleware_service.exception.CustomException;
import com.bishop.signature_middleware_service.repository.TransactionDetailsRepository;
import com.bishop.signature_middleware_service.service.DatabaseService;
import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.bishop.signature_middleware_service.config.ApplicationConstants.DEFAULT_DATABASE_ERROR;
import static com.bishop.signature_middleware_service.config.ApplicationConstants.DUPLICATE_RECORD;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    private static final Logger log = LoggerFactory.getLogger(DatabaseServiceImpl.class);

    private final TransactionDetailsRepository transactionDetailsRepository;

    @Autowired
    public DatabaseServiceImpl(@Lazy TransactionDetailsRepository transactionMasterRepository) {
        this.transactionDetailsRepository = transactionMasterRepository;
    }

    // Use @Transactional(readOnly = true) because this is a pure database read operation
    @Override
    @Transactional(readOnly = true)
    public void checkTransactionExists(String rrn, TransactionType type) throws CustomException {
        boolean recordExists = transactionDetailsRepository.existsByRrnAndTransactionType(rrn, type);

        if (recordExists) {
            log.error("{}: Duplicate transaction found for RRN: {}", rrn, rrn);
            throw new CustomException(DUPLICATE_RECORD);
        }
    }

    // Use @Transactional because we want the saveAndFlush operation to happen inside a database transaction
    @Override
    @Transactional
    public void saveCreditTransferEntity(String rrn, TransactionDetails entity) throws CustomException {
        try {
            transactionDetailsRepository.saveAndFlush(entity);
            log.info("{}: Successfully persisted initial transaction record with RRN: {}", rrn, rrn);
        } catch (DataIntegrityViolationException e) {
            log.error("{}: Integrity violation when saving transaction entity: {}", rrn, e.getMessage());
            throw new CustomException(DEFAULT_DATABASE_ERROR + e.getMessage());
        } catch (PersistenceException | DataAccessException e) {
            log.error("{}: Persistence error occurred: {}", rrn, e.getMessage());
            throw new CustomException(DEFAULT_DATABASE_ERROR + e.getMessage());
        } catch (Exception e) {
            log.error("{}: Unexpected error occurred: {}", rrn, e.getMessage());
            throw new CustomException(DEFAULT_DATABASE_ERROR + e.getMessage());
        }
    }
}
