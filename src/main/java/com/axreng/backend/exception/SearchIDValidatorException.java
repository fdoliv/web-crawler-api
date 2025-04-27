package com.axreng.backend.exception;

public class SearchIDValidatorException extends Exception {
    public SearchIDValidatorException(String message) {
        super(message);
    }

    public SearchIDValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

}
