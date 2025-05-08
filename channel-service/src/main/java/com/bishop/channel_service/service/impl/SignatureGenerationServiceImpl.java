package com.bishop.channel_service.service.impl;

import com.bishop.channel_service.dto.ChannelRequestDto;
import com.bishop.channel_service.enums.TransactionType;
import com.bishop.channel_service.exception.CustomException;
import com.bishop.channel_service.service.SignatureGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;

import static com.bishop.channel_service.config.ApplicationConstants.CERT_GENERATION_ERROR;
import static com.bishop.channel_service.config.ApplicationConstants.CHANNEL_ID;

@Service
public class SignatureGenerationServiceImpl implements SignatureGenerationService {
    private static final Logger log = LoggerFactory.getLogger(SignatureGenerationServiceImpl.class);

    private final String keyAlias;
    private final String keyStorePath;
    private final String keyStoreType;
    private final String keyStorePassword;

    public SignatureGenerationServiceImpl(@Value("${server.ssl.key-alias}") String keyAlias,
                                          @Value("${server.ssl.key-store}") String keyStorePath,
                                          @Value("${server.ssl.key-store-type}") String keyStoreType,
                                          @Value("${server.ssl.key-store-password}") String keyStorePassword) {
        this.keyAlias = keyAlias;
        this.keyStorePath = keyStorePath;
        this.keyStoreType = keyStoreType;
        this.keyStorePassword = keyStorePassword;
    }

    @Override
    public String generateSignature(ChannelRequestDto requestDto, TransactionType type) throws CustomException {
        try (FileInputStream fis = new FileInputStream(keyStorePath)) {
            // Load keystore from file
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(fis, keyStorePassword.toCharArray());

            // Retrieve private key using alias and password
            Key key = keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
            if (!(key instanceof PrivateKey)) {
                throw new CustomException(CERT_GENERATION_ERROR + "Key in keystore is not a private key");
            }

            // Concatenate fields in exact order to create the clear text
            String clearText = generateClearTextValues(requestDto);
            log.info("Data to sign: {}", clearText);

            // Initialize and sign the data using RSA + SHA-256
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign((PrivateKey) key);
            signature.update(clearText.getBytes());

            // Encode the resulting signature in Base64
            byte[] signedBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signedBytes);

        } catch (KeyStoreException
                 | NoSuchAlgorithmException
                 | UnrecoverableKeyException
                 | CertificateException
                 | InvalidKeyException
                 | SignatureException
                 | IOException e) {
            log.error("Error generating signature: {}", e.getMessage());
            throw new CustomException(CERT_GENERATION_ERROR + "Signature generation failed: " + e.getMessage());
        }
    }

    private String generateClearTextValues(ChannelRequestDto requestDto) {
        // Compose canonical string to be signed (order matters)
        return requestDto.getRrn() +
                CHANNEL_ID +
                requestDto.getSenderName() +
                requestDto.getReceiverName() +
                requestDto.getAmount() +
                requestDto.getFeeAmount() +
                requestDto.getCurrency();
    }
}
