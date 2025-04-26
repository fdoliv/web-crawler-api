package com.axreng.backend.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

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
        // When
        boolean isValid = SearchIDValidator.validate(id);

        // Then
        assertThat("Validation failed for id: " + id, isValid, is(expected));
    }
}
