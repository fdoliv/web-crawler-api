package br.dev.dias.util;

import org.junit.jupiter.api.Test;

import br.dev.dias.util.IDGenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class IDGeneratorTest {

    private final int ID_LENGTH = 8;
    private final String ALPHANUMERIC_REGEX = "^[a-zA-Z0-9]+$";

    @Test
    void shouldGenerateIdWithCorrectLength() {
        // When
        String id = IDGenerator.generateAlphanumericID();

        // Then
        assertThat("ID should have a length of 8 characters", id.length(), is(ID_LENGTH));
    }

    @Test
    void shouldGenerateAlphanumericId() {
        // When
        String id = IDGenerator.generateAlphanumericID();

        // Then
        assertThat("ID should be alphanumeric", id.matches(ALPHANUMERIC_REGEX), is(true));
    }

    @Test
    void shouldGenerateUniqueIds() {
        // When
        String id1 = IDGenerator.generateAlphanumericID();
        String id2 = IDGenerator.generateAlphanumericID();

        // Then
        assertThat("IDs should be unique", id1, is(not(id2)));
    }
}
