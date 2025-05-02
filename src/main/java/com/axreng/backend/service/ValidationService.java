package com.axreng.backend.service;

import com.axreng.backend.exception.KeywordValidatorException;
import com.axreng.backend.exception.SearchIDValidatorException;

public class ValidationService {

    private static final String ID_INPUT_PATTERN = "^[a-zA-Z0-9]{8}$";
    private static final int ID_MIN_LENGTH = 8;
    private static final int ID_MAX_ID_LENGTH = 8;
    private static final int KEYWORD_MIN_LENGTH = 4;
    private static final int KEYWORD_MAX_LENGTH = 32;

    public void validateKeyword(String keyword) throws KeywordValidatorException {
        if (keyword == null) {
            throw new KeywordValidatorException("Invalid keyword: cannot be null.");
        }
        if (keyword.isBlank()) {
            throw new KeywordValidatorException("Invalid keyword: cannot be empty.");
        }
        if (keyword.length() < KEYWORD_MIN_LENGTH) {
            throw new KeywordValidatorException("Invalid keyword: too short.");
        }
        if (keyword.length() > KEYWORD_MAX_LENGTH) {  
            throw new KeywordValidatorException("Invalid keyword: too long.");
        }
    }

    public void validateSearchId(String searchId) throws SearchIDValidatorException {
        if(searchId == null) {
            throw new SearchIDValidatorException("Invalid search ID: cannot be null.");
        }
        if(searchId.isBlank()) {
            throw new SearchIDValidatorException("Invalid search ID: cannot be empty.");
        }
        if(searchId.length() < ID_MIN_LENGTH) {
            throw new SearchIDValidatorException("Invalid search ID: too short.");
        }
        if(searchId.length() > ID_MAX_ID_LENGTH) {  
            throw new SearchIDValidatorException("Invalid search ID: too long.");
        }
        if(!searchId.matches(ID_INPUT_PATTERN)) {
            throw new SearchIDValidatorException("Invalid search ID: must be alphanumeric.");
        }  
    }
}
