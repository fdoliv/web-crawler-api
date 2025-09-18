package br.dev.dias.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import br.dev.dias.exception.KeywordValidatorException;
import br.dev.dias.exception.SearchIDValidatorException;
import br.dev.dias.service.ValidationService;


public class ValidatorServiceTest {


    private static ValidationService validationService;

    private static final String KEYWORD_TEST_CASES = "/keyword_test_cases.csv";
    private static final String ID_TEST_CASES = "/id_test_cases.csv";
    @BeforeAll
    static void setUp() {
        
        validationService = new ValidationService();
        
    }

    @ParameterizedTest
    @CsvFileSource(resources = KEYWORD_TEST_CASES, numLinesToSkip = 1)
    @DisplayName("Should validate keywords correctly from CSV file")
    void shouldValidateKeywordsFromCsv(String keyword, boolean expected) {
        if (expected) {
            // When: Must not throw an exception
            var isValid = true;
            try{
                validationService.validateKeyword(keyword);
                
            } catch (KeywordValidatorException e) {
                isValid = false;
            }
            // Then: 
            assertThat("Keyword should be valid: " + keyword, isValid, is(true));
            
        } else {
            // When: Must throw an exception
            KeywordValidatorException exception = assertThrows(
                KeywordValidatorException.class, 
                () -> validationService.validateKeyword(keyword)
            );
            // Then: Message should indicate invalid keyword
            assertThat("Exception message should indicate invalid keyword", 
                exception.getMessage(), containsString("Invalid keyword"));
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = ID_TEST_CASES, numLinesToSkip = 1)
    @DisplayName("Should validate ids correctly from CSV file")
    void shouldValidateIdsFromCsv(String id, boolean expected) {
        
        if (expected) {
            // When: Must not throw an exception
            var isValid = true;
            try{
                validationService.validateSearchId(id);

            } catch (SearchIDValidatorException e) {
                isValid = false;
            }
            // Then: 
            assertThat("Keyword should be valid: " + id, isValid, is(true));
            
        } else {
            // When: Must throw an exception
            SearchIDValidatorException exception = assertThrows(
                SearchIDValidatorException.class, 
                () -> validationService.validateSearchId(id)
            );
            // Then: Message should indicate invalid keyword
            assertThat("Exception message should indicate invalid search ID", 
                exception.getMessage(), containsString("Invalid search ID"));
        }
    }
}
