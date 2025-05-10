package com.bishop.signature_middleware_service.repository;

import com.bishop.signature_middleware_service.entity.TransactionDetails;
import com.bishop.signature_middleware_service.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, Long> {
    // Exists check for duplicate check
    boolean existsByRrnAndTransactionType(String rrn, TransactionType transactionType);
}
