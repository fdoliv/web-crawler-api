package com.axreng.backend.util;

import com.axreng.backend.exception.KeywordValidatorException;

public class KeywordValidator {
    
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 32;

    public static void validate(String input) throws KeywordValidatorException {

        if (input == null) {
            throw new KeywordValidatorException("Invalid keyword: cannot be null.");
        }
        if (input.isBlank()) {
            throw new KeywordValidatorException("Invalid keyword: cannot be empty.");
        }
        if (input.length() < MIN_LENGTH) {
            throw new KeywordValidatorException("Invalid keyword: too short.");
        }
        if (input.length() > MAX_LENGTH) {  
            throw new KeywordValidatorException("Invalid keyword: too long.");
        }
    }
}