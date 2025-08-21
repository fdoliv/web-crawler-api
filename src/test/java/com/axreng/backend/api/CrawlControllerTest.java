package com.axreng.backend.api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.axreng.backend.helper.JsonHelper;
import com.fdoliv.backend.api.CrawlController;
import com.fdoliv.backend.service.CrawlerService;
import com.fdoliv.backend.service.HttpClientService;
import com.fdoliv.backend.service.KeywordSearchService;
import com.fdoliv.backend.service.LinkExtractorService;
import com.fdoliv.backend.service.SearchService;
import com.fdoliv.backend.service.ValidationService;
import com.fdoliv.backend.util.ApplicationConfiguration;
import com.fdoliv.backend.util.HttpClientHelper;
import com.fdoliv.backend.util.HttpMethods;
import com.fdoliv.backend.util.HttpResponseCode;
import com.fdoliv.backend.util.HttpResponseReader;

import java.net.HttpURLConnection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class CrawlControllerTest {

    HttpClientHelper httpClientHelper = new HttpClientHelper();
    HttpResponseReader httpResponseReader = new HttpResponseReader();

    private final String BASE_URL = "http://localhost:4567";

    private final int HTTP_CONNECTION_TIMEOUT = 5000;
    private final int HTTP_READ_TIMEOUT = 5000;

    @BeforeAll
    static void setUp() {
        ApplicationConfiguration appConfig = mock(ApplicationConfiguration.class);
        when(appConfig.getBaseUrl()).thenReturn("http://localhost:8080");
        when(appConfig.getMaxThreads()).thenReturn(10);
        when(appConfig.getMinThreads()).thenReturn(2);

        SearchService searchService = new SearchService();
        ValidationService validationService = new ValidationService();
        KeywordSearchService keywordSearchService = new KeywordSearchService();
        LinkExtractorService linkExtractorService = new LinkExtractorService();
        HttpClientService httpClientService = new HttpClientService();
        CrawlerService crawlerService = new CrawlerService(searchService, keywordSearchService, linkExtractorService, httpClientService, appConfig);
        CrawlController crawlController = new CrawlController(searchService, crawlerService, validationService);
        crawlController.initializeRoutes();
        spark.Spark.awaitInitialization();
    }

    @AfterAll
    static void tearDown() {
        spark.Spark.stop();
    }

    @Test
    @DisplayName("Should return 200 OK and id for valid keyword")
    void shouldReturn200ForValidKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl";
        var expectedHttpResponseCode = HttpResponseCode.OK;
        var expectedContentType = "application/json";
        var expectedResponseBody = "{\"id\":";
        var requestBody = "{ \"keyword\": \"validKeyword\" }";

        HttpURLConnection connection = null;
        try {
            connection = httpClientHelper.createConnection(BASE_URL + BASE_PATH, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            // When
            int responseCode = connection.getResponseCode();
            String responseBody = httpResponseReader.readResponse(connection);
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat("Response code should indicate OK code", responseCode, is(expectedHttpResponseCode));
            assertThat("Response body should indicate the id", responseBody, containsString(expectedResponseBody));
            assertThat("Content-type should be application/json", contentType, is(expectedContentType));
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid keyword")
    void shouldReturn400ForInvalidKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl";
        var expectedHttpResponseCode = HttpResponseCode.BAD_REQUEST;
        var expectedContentType = "application/json";
        var expectedResponseBody = "too short";
        String requestBody = "{ \"keyword\": \"abc\" }"; 
        HttpURLConnection connection = null;

        try{
            connection = httpClientHelper.createConnection(BASE_URL + BASE_PATH, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

            // When
            int responseCode = connection.getResponseCode();
            String responseBody = httpResponseReader.readResponse(connection);
            String contentType = connection.getHeaderField("Content-Type");

    
            // Then
            assertThat("Response code should indicate BAD_REQUEST code", responseCode, is(expectedHttpResponseCode));
            assertThat("Response message should indicate that keyword is too short", responseBody, containsString(expectedResponseBody));
            assertThat("Content-type should be application/json", contentType, is(expectedContentType));

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request for missing keyword")
    void shouldReturn400ForMissingKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl";
        var expectedHttpResponseCode = HttpResponseCode.BAD_REQUEST;
        var expectedContentType = "application/json";
        var expectedResponseBody = "Invalid keyword";
        String requestBody = "{ }";
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(BASE_URL + BASE_PATH, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

            // When
            int responseCode = connection.getResponseCode();
            String responseBody = httpResponseReader.readResponse(connection);
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat("Response code should indicate BAD_REQUEST code", responseCode, is(expectedHttpResponseCode));
            assertThat("Response message should indicate that keyword is invalid", responseBody, containsString(expectedResponseBody));
            assertThat("Content-type should be application/json", contentType, is(expectedContentType));
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return Content-Type as application/json")
    void shouldReturnContentTypeAsApplicationJson() throws Exception {
        // Given
        String requestBody = "{ \"keyword\": \"validKeyword\" }";
        HttpURLConnection connection = httpClientHelper.createConnection("http://localhost:4567/crawl", "POST", requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

        // When
        connection.getResponseCode();
        String contentType = connection.getHeaderField("Content-Type");

        // Then
        assertThat("Content-type shoud be application/json", contentType, is("application/json"));
    }

    @Test
    @DisplayName("Should return 200 OK for valid ID")
    void shouldReturn200ForValidId() throws Exception {
        // Given
        String requestBody = "{ \"keyword\": \"validKeyword\" }";
        HttpURLConnection postConnection = httpClientHelper.createConnection("http://localhost:4567/crawl", "POST", requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
        postConnection.getResponseCode();
        String postResponseBody = httpResponseReader.readResponse(postConnection);
        String id = JsonHelper.extractFieldFromJson(postResponseBody, "id");

        // When
        HttpURLConnection getConnection = httpClientHelper.createConnection("http://localhost:4567/crawl/" + id, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
        int responseCode = getConnection.getResponseCode();
        String contentType = getConnection.getHeaderField("Content-Type");


        // Then
        assertThat("Response code should indicate OK code", responseCode, is(HttpResponseCode.OK));
        assertThat("Content-type shoud be application/json", contentType, is("application/json"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for short ID")
    void shouldReturn400ForShortId() throws Exception {
        // When
        HttpURLConnection connection = httpClientHelper.createConnection("http://localhost:4567/crawl/1342", "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
        int responseCode = connection.getResponseCode();
        String responseBody = httpResponseReader.readResponse(connection);
        String contentType = connection.getHeaderField("Content-Type");

        // Then
        assertThat("Response code should indicate BAD_REQUEST code", responseCode, is(HttpResponseCode.BAD_REQUEST));
        assertThat("Response message should indicate that id is too short", responseBody, containsString("too short"));
        assertThat("Content-type shoud be application/json", contentType, is("application/json"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for long ID")
    void shouldReturn400ForLongId() throws Exception {
        // When
        HttpURLConnection connection = httpClientHelper.createConnection("http://localhost:4567/crawl/longidsended", "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
        int responseCode = connection.getResponseCode();
        String responseBody = httpResponseReader.readResponse(connection);
        String contentType = connection.getHeaderField("Content-Type");

        // Then
        assertThat("Response code should indicate BAD_REQUEST code", responseCode, is(HttpResponseCode.BAD_REQUEST));
        assertThat("Response message should indicate that id is too long", responseBody, containsString("too long"));
        assertThat("Content-type shoud be application/json", contentType, is("application/json"));
    }

    @Test
    @DisplayName("Should return 404 Not Found for non-existent ID")
    void shouldReturn404ForNonExistentId() throws Exception {
        
        // When
        HttpURLConnection connection = httpClientHelper.createConnection("http://localhost:4567/crawl/1234abcd", "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
        int responseCode = connection.getResponseCode();
        String responseBody = httpResponseReader.readResponse(connection);
        String contentType = connection.getHeaderField("Content-Type");

        // Then
        assertThat("Response code should indicate NOT_FOUND code", responseCode, is(HttpResponseCode.NOT_FOUND));
        assertThat("Response message should indicate that id is not found", responseBody, containsString("not found"));
        assertThat("Content-type shoud be application/json", contentType, is("application/json"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request for ID with non-alphanumeric characters")
    void shouldReturn400ForNonAlphanumericId() throws Exception {
        // When
        HttpURLConnection connection = httpClientHelper.createConnection("http://localhost:4567/crawl/123$abcd", "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
        int responseCode = connection.getResponseCode();
        String responseBody = httpResponseReader.readResponse(connection);
        String contentType = connection.getHeaderField("Content-Type");

        // Then
        assertThat("Response code should indicate BAD_REQUEST", responseCode, is(400));
        assertThat("Response message should indicate that id must be alphanumeric",responseBody, containsString("must be alphanumeric"));
        assertThat("Content-type shoud be application/json", contentType, is("application/json"));
    }
}
