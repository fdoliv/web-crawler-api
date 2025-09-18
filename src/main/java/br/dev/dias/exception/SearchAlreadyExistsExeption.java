package br.dev.dias.exception;

public class SearchAlreadyExistsExeption extends Exception {

    public SearchAlreadyExistsExeption(String message) {
        super(message);
    }

    public SearchAlreadyExistsExeption(String message, Throwable cause) {
        super(message, cause);
    }



}
