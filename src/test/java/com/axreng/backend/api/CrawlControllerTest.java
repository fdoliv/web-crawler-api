package com.axreng.backend.api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.axreng.backend.helper.HttpClientHelper;

import java.net.HttpURLConnection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class CrawlControllerTest {

    HttpClientHelper helper = HttpClientHelper.getInstance();

    @BeforeAll
    static void setUp() {
        
        CrawlController.initializeRoutes();
        spark.Spark.awaitInitialization(); 
    }

    @AfterAll
    static void tearDown() {
        spark.Spark.stop(); 
    }

    @Test
    @DisplayName("Should return 200 OK for valid keyword")
    void shouldReturn200ForValidKeyword() throws Exception {
        // Given
        String requestBody = "{ \"keyword\": \"validKeyword\" }";
        HttpURLConnection connection = helper.createConnection("http://localhost:4567/crawl", "POST", requestBody);

        // When
        int responseCode = connection.getResponseCode();
        String responseBody = helper.readResponse(connection);

        // Then
        assertThat(responseCode, is(200));
        assertThat(responseBody, containsString("{\"id\":"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid keyword")
    void shouldReturn400ForInvalidKeyword() throws Exception {
        // Given
        String requestBody = "{ \"keyword\": \"abc\" }"; // Menos de 4 caracteres
        HttpURLConnection connection = helper.createConnection("http://localhost:4567/crawl", "POST", requestBody);

        // When
        int responseCode = connection.getResponseCode();
        String responseBody = helper.readResponse(connection);

        // Then
        assertThat(responseCode, is(400));
        assertThat(responseBody, containsString("too short"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for missing keyword")
    void shouldReturn400ForMissingKeyword() throws Exception {
        // Given
        String requestBody = "{ }"; 
        HttpURLConnection connection = helper.createConnection("http://localhost:4567/crawl", "POST", requestBody);

        // When
        int responseCode = connection.getResponseCode();
        String responseBody = helper.readResponse(connection);

        // Then
        assertThat(responseCode, is(400));
        assertThat(responseBody, containsString("Invalid keyword"));
    }
}
