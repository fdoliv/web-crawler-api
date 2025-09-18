package br.dev.dias.util;

import org.junit.jupiter.api.Test;

import br.dev.dias.model.Search;
import br.dev.dias.model.Status;
import br.dev.dias.util.ResponseHelper;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ResponseHelperTest {

    @Test
    void shouldCreateSuccessResponse() {
        String response = ResponseHelper.createSuccessResponse("123");

        assertThat("Response should not be null", response, is(notNullValue()));
        assertThat("Response should contain the ID", response, containsString("123"));
    }

    @Test
    void shouldCreateCrawlStatusResponse() {
        Search search = new Search("123", "keyword", Status.ACTIVE);
        search.setUrls(Set.of("http://example.com"));

        String response = ResponseHelper.createCrawlStatusResponse(search);

        assertThat("Response should not be null", response, is(notNullValue()));
        assertThat("Response should contain the search ID", response, containsString("123"));
        assertThat("Response should contain the keyword", response, containsString("keyword"));
        assertThat("Response should contain the URL", response, containsString("http://example.com"));
    }
}
