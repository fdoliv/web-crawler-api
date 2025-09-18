package br.dev.dias.exception;

public class SearchAlreadyExistsException extends Exception {

    public SearchAlreadyExistsException(String message) {
        super(message);
    }

    public SearchAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }



}
