package br.dev.dias.service;

import br.dev.dias.exception.KeywordValidatorException;
import br.dev.dias.exception.SearchIDValidatorException;

/**
 * Service for validating keywords and search IDs.
 * Ensures that inputs meet specific length and format requirements.
 */
public class ValidationService {

    /**
     * Regex pattern for validating search IDs. Must be exactly 8 alphanumeric characters.
     */
    private static final String ID_INPUT_PATTERN = "^[a-zA-Z0-9]{8}$";

    /**
     * Minimum length for a valid search ID.
     */
    private static final int ID_MIN_LENGTH = 8;

    /**
     * Maximum length for a valid search ID.
     */
    private static final int ID_MAX_ID_LENGTH = 8;

    /**
     * Minimum length for a valid keyword.
     */
    private static final int KEYWORD_MIN_LENGTH = 4;

    /**
     * Maximum length for a valid keyword.
     */
    private static final int KEYWORD_MAX_LENGTH = 32;

    /**
     * Validates the given keyword to ensure it meets length and non-null requirements.
     *
     * @param keyword the keyword to validate
     * @throws KeywordValidatorException if the keyword is null, empty, too short, or too long
     */
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

    /**
     * Validates the given search ID to ensure it meets length, format, and non-null requirements.
     *
     * @param searchId the search ID to validate
     * @throws SearchIDValidatorException if the search ID is null, empty, too short, too long, or not alphanumeric
     */
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
