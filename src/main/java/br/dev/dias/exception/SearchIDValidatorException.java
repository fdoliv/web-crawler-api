package br.dev.dias.exception;

public class SearchIDValidatorException extends ValidationException {
    public SearchIDValidatorException(String message) {
        super(message);
    }

    public SearchIDValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

}
