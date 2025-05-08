package com.bishop.jwt_middleware_service.dto;

import java.io.Serializable;

public class TransactionResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rrn;
    private String status;
    private String responseCode;
    private String responseDesc;

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseDesc() {
        return responseDesc;
    }

    public void setResponseDesc(String responseDesc) {
        this.responseDesc = responseDesc;
    }
}