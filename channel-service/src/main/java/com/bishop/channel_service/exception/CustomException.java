package com.bishop.channel_service.exception;

public class CustomException extends Exception {
    private final String message;

    public CustomException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
