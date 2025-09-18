package br.dev.dias.util;

import org.junit.jupiter.api.Test;

import br.dev.dias.util.HttpResponseReader;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class HttpResponseReaderTest {

    private final HttpResponseReader reader = new HttpResponseReader();

    @Test
    void shouldReadResponseFromConnection() throws Exception {
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getResponseCode()).thenReturn(200);
        when(connection.getInputStream()).thenReturn(new ByteArrayInputStream("response".getBytes()));

        String response = reader.readResponse(connection);

        assertThat("Response should match expected content", response, is("response"));
    }
}
