package com.axreng.backend.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.axreng.backend.exception.SearchIDValidatorException;

/**
 * Test class for validating the functionality of the SearchIDValidator.
 * 
 * This class uses parameterized tests with data provided from a CSV file
 * to ensure that the SearchIDValidator correctly validates IDs.
 * 
 * The CSV file should contain test cases with two columns:
 * - The first column represents the ID to be validated.
 * - The second column represents the expected validation result (true for valid, false for invalid).
 * 
 * An ID is considered valid if it consists of exactly 8 alphanumeric characters.
 * Otherwise, it is considered invalid.
 * 
 * Each test case is executed to verify that the validation logic behaves as expected.
 */
public class SearchIDValidatorTest {


    @ParameterizedTest
    @CsvFileSource(resources = "/id_test_cases.csv", numLinesToSkip = 1)
    @DisplayName("Should validate ids correctly from CSV file")
    void shouldValidateKeywordsFromCsv(String id, boolean expected) {
        
        if (expected) {
            // When: Must not throw an exception
            var isValid = true;
            try{
                SearchIDValidator.validate(id);

            } catch (SearchIDValidatorException e) {
                isValid = false;
            }
            // Then: 
            assertThat("Keyword should be valid: " + id, isValid, is(true));
            
        } else {
            // When: Must throw an exception
            SearchIDValidatorException exception = assertThrows(
                SearchIDValidatorException.class, 
                () -> SearchIDValidator.validate(id)
            );
            // Then: Message should indicate invalid keyword
            assertThat("Exception message should indicate invalid search ID", 
                exception.getMessage(), containsString("Invalid search ID"));
        }
    }
}
