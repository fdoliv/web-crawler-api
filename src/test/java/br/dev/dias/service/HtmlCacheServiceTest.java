package br.dev.dias.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.dev.dias.service.HtmlCacheService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class HtmlCacheServiceTest {

    private HtmlCacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new HtmlCacheService();
    }

    @AfterEach
    void tearDown() {
        cacheService.shutdown();
    }

    @Test
    void shouldStoreAndRetrieveContent() {
        String url = "http://example.com";
        String content = "<html>Example</html>";

        cacheService.put(url, content);
        String cachedContent = cacheService.get(url);

        assertThat("Content should be retrieved from cache", cachedContent, is(content));
    }

    @Test
    void shouldReturnNullForExpiredContent() throws InterruptedException {
        String url = "http://example.com";
        String content = "<html>Example</html>";

        cacheService.put(url, content);
        Thread.sleep(310000); // Wait for 5+ minutes
        String cachedContent = cacheService.get(url);

        assertThat("Content should be null after expiration", cachedContent, is(nullValue()));
    }
}
