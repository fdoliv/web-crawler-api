package com.axreng.backend.util;

import org.junit.jupiter.api.Test;

import com.fdoliv.backend.util.HttpClientHelper;

import java.net.HttpURLConnection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpClientHelperTest {

    private final HttpClientHelper httpClientHelper = new HttpClientHelper();

    @Test
    void shouldThrowExceptionForInvalidUrl() {
        assertThrows(Exception.class, () -> {
            httpClientHelper.createConnection("invalid-url", "GET", null, 5000, 5000);
        });
    }

    @Test
    void shouldCreateConnectionForValidUrl() throws Exception {
        String validUrl = "https://httpbin.org/get"; // Public test endpoint
        HttpURLConnection connection = httpClientHelper.createConnection(validUrl, "GET", null, 5000, 5000);

        assertThat("Connection should not be null", connection, is(notNullValue()));
        assertThat("Response code should be 200", connection.getResponseCode(), is(200));

        connection.disconnect();
    }
}
