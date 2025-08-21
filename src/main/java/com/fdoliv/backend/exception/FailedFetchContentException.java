package com.fdoliv.backend.exception;

public class FailedFetchContentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FailedFetchContentException(String message) {
        super(message);
    }

    public FailedFetchContentException(String message, Throwable cause) {
        super(message, cause);
    }

}
