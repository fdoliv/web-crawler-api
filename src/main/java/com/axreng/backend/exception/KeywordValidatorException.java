package com.axreng.backend.exception;

public class KeywordValidatorException extends ValidationException {
    public KeywordValidatorException(String message) {
        super(message);
    }

    public KeywordValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

}
