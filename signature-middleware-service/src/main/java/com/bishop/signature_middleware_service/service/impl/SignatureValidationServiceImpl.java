package com.bishop.signature_middleware_service.service.impl;

import com.bishop.signature_middleware_service.dto.TransactionRequestDto;
import com.bishop.signature_middleware_service.enums.TransactionType;
import com.bishop.signature_middleware_service.exception.CustomException;
import com.bishop.signature_middleware_service.service.SignatureValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

import static com.bishop.signature_middleware_service.config.ApplicationConstants.*;

@Service
public class SignatureValidationServiceImpl implements SignatureValidationService {
    private static final Logger log = LoggerFactory.getLogger(SignatureValidationServiceImpl.class);

    private final String trustStorePath;
    private final String trustStoreType;
    private final String trustStorePassword;

    // Map channelId to its corresponding certificate alias in the trust store
    private final Map<String, String> channelCertAliasMap = Map.of(
            "OMNI", "omni",
            "REMITTANCE", "remittance"
    );

    public SignatureValidationServiceImpl(@Value("${server.verification.trust-store}") String trustStorePath,
                                          @Value("${server.verification.trust-store-type}") String trustStoreType,
                                          @Value("${server.verification.trust-store-password}") String trustStorePassword) {
        this.trustStorePath = trustStorePath;
        this.trustStoreType = trustStoreType;
        this.trustStorePassword = trustStorePassword;
    }

    @Override
    public void validateSignature(TransactionRequestDto requestDto, TransactionType type) throws CustomException {
        String rrn = requestDto.getRrn();
        String channelId = requestDto.getChannelId();

        // Resolve the correct certificate alias based on channelId
        String resolvedAlias = channelCertAliasMap.get(channelId);
        if (resolvedAlias == null) {
            throw new CustomException(CERT_VERIFICATION_ERROR + "Unrecognized channelId: " + channelId);
        }

        try (FileInputStream fis = new FileInputStream(trustStorePath)) {
            // Load the trust store
            KeyStore trustStore = KeyStore.getInstance(trustStoreType);
            trustStore.load(fis, trustStorePassword.toCharArray());

            // Retrieve the certificate using the resolved alias
            X509Certificate cert = (X509Certificate) trustStore.getCertificate(resolvedAlias);
            if (cert == null) {
                throw new CustomException(CERT_VERIFICATION_ERROR + "Certificate not found for alias: " + resolvedAlias);
            }

            // Get the public key from the certificate
            PublicKey publicKey = cert.getPublicKey();

            // Construct the original clear text used during signing
            String clearText = generateClearTextValues(requestDto);
            log.info("Clear text for verification: {}", clearText);

            // Decode the Base64-encoded signature
            byte[] signatureBytes = Base64.getDecoder().decode(requestDto.getSignature());

            // Initialize the signature verification process
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(clearText.getBytes());

            // Perform signature validation
            if (!signature.verify(signatureBytes)) {
                throw new CustomException(CERT_VERIFICATION_ERROR + "Invalid signature");
            }

            log.info("{}: Signature validated successfully using alias '{}'", rrn, resolvedAlias);
        } catch (KeyStoreException
                 | NoSuchAlgorithmException
                 | CertificateException
                 | IOException
                 | InvalidKeyException
                 | SignatureException e) {
            // Log and rethrow custom exception if validation fails
            log.error("{}: Signature verification failed: {}", rrn, e.getMessage());
            throw new CustomException(CERT_VERIFICATION_ERROR + "Signature validation failed: " + e.getMessage());
        }
    }

    // Builds the canonical clear text string used for signature validation
    private String generateClearTextValues(TransactionRequestDto requestDto) {
        return requestDto.getRrn() +
                CHANNEL_ID +
                requestDto.getSenderName() +
                requestDto.getReceiverName() +
                requestDto.getAmount() +
                requestDto.getFeeAmount() +
                requestDto.getCurrency();
    }
}
