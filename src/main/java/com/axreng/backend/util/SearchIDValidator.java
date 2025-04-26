package com.axreng.backend.util;

public class SearchIDValidator {
    
    private static final String INPUT_PATTERN = "^[a-zA-Z0-9]{8}$";
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 8;
    
    public static boolean validate(String input) {
        return input != null && input.matches(INPUT_PATTERN) 
        && input.length() >= MIN_LENGTH 
        && input.length() <= MAX_LENGTH;
    }
}
