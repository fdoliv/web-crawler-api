package com.axreng.backend.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * Test class for validating the functionality of the KeywordValidator.
 * 
 * This class uses parameterized tests with data provided from a CSV file
 * to ensure that the KeywordValidator correctly validates search keywords.
 * 
 * The CSV file should contain test cases with two columns:
 * - The first column represents the search keyword to be validated.
 * - The second column represents the expected validation result (true for valid, false for invalid).
 * 
 * A search term is considered valid if it has a minimum of 4 characters and a maximum of 32 characters.
 * Otherwise, it is considered invalid.
 * 
 * Each test case is executed to verify that the validation logic behaves as expected.
 */
public class KeywordValidatorTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/keyword_test_cases.csv", numLinesToSkip = 1)
    @DisplayName("Should validate keywords correctly from CSV file")
    void shouldValidateKeywordsFromCsv(String keyword, boolean expected) {
        // When
        boolean isValid = KeywordValidator.validate(keyword);

        // Then
        assertThat("Validation failed for keyword: " + keyword, isValid, is(expected));
    }
}
