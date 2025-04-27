package com.axreng.backend.util;

public class KeywordValidator {
    
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 32;

    public static boolean validate(String input) {
        return input != null 
            && !input.isBlank()
            && input.length() >= MIN_LENGTH
            && input.length() <= MAX_LENGTH;
    }
}