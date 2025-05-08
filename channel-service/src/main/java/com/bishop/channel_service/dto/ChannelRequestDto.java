package com.bishop.channel_service.dto;
import com.bishop.channel_service.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.math.BigDecimal;

public class ChannelRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "RRN must not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "RRN must be alphanumeric and 8–20 characters")
    private String rrn;

    @NotBlank(message = "Sender name must not be blank")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']{2,50}$", message = "Sender name contains invalid characters")
    private String senderName;

    @NotBlank(message = "Receiver name must not be blank")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']{2,50}$", message = "Receiver name contains invalid characters")
    private String receiverName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Fee amount is required")
    @DecimalMin(value = "0.0", message = "Fee amount cannot be negative")
    private BigDecimal feeAmount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter uppercase code like 'KES'")
    private String currency;

    @NotBlank(message = "Narration must not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,'!?@()\\-]{2,200}$", message = "Narration contains invalid characters")
    private String narration;

    // Optional fields
    private String channelId;
    private String signature;
    private TransactionType transactionType;

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
}