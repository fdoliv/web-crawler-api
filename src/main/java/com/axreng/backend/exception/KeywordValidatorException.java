package com.axreng.backend.exception;

public class KeywordValidatorException extends Exception {
    public KeywordValidatorException(String message) {
        super(message);
    }

    public KeywordValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

}
