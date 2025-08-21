package com.fdoliv.backend.exception;

public class HttpRequestFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HttpRequestFailedException(String message) {
        super(message);
    }

    public HttpRequestFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
