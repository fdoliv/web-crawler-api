package br.dev.dias.exception;

public class SearchNotFoundException extends Exception {

    public SearchNotFoundException(String message) {
        super(message);
    }

    public SearchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
