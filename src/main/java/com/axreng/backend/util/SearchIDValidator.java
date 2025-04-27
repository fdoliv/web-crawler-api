package com.axreng.backend.util;

import com.axreng.backend.exception.SearchIDValidatorException;

public class SearchIDValidator {
    
    private static final String INPUT_PATTERN = "^[a-zA-Z0-9]{8}$";
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 8;
    
    public static void validate(String input) throws SearchIDValidatorException{

        if(input == null) {
            throw new SearchIDValidatorException("Invalid search ID: cannot be null.");
        }
        if(input.isBlank()) {
            throw new SearchIDValidatorException("Invalid search ID: cannot be empty.");
        }
        if(input.length() < MIN_LENGTH) {
            throw new SearchIDValidatorException("Invalid search ID: too short.");
        }
        if(input.length() > MAX_LENGTH) {  
            throw new SearchIDValidatorException("Invalid search ID: too long.");
        }
        if(!input.matches(INPUT_PATTERN)) {
            throw new SearchIDValidatorException("Invalid search ID: must be alphanumeric.");
        }   

    }
}
