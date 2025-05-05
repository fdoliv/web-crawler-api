package com.axreng.backend.service;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class KeywordSearchServiceTest {

    private final KeywordSearchService keywordSearchService = new KeywordSearchService();

    @Test
    void shouldFindKeywordInContent() {
        String content = "This is a test content.";
        String keyword = "test";

        boolean result = keywordSearchService.containsKeyword(content, keyword);

        assertThat("Keyword should be found in content", result, is(true));
    }

    @Test
    void shouldNotFindKeywordInContent() {
        String content = "This is a test content.";
        String keyword = "missing";

        boolean result = keywordSearchService.containsKeyword(content, keyword);

        assertThat("Keyword should not be found in content", result, is(false));
    }
}
