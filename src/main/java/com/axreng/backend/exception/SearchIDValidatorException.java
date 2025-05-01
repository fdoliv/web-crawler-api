package com.axreng.backend.exception;

public class SearchIDValidatorException extends ValidationException {
    public SearchIDValidatorException(String message) {
        super(message);
    }

    public SearchIDValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

}
