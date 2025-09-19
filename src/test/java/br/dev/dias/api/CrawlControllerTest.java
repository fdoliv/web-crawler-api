package br.dev.dias.api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.dev.dias.helper.JsonHelper;
import br.dev.dias.service.CrawlerService;
import br.dev.dias.service.HttpClientService;
import br.dev.dias.service.KeywordSearchService;
import br.dev.dias.service.LinkExtractorService;
import br.dev.dias.service.SearchService;
import br.dev.dias.service.ValidationService;
import br.dev.dias.util.ApplicationConfiguration;
import br.dev.dias.util.HttpClientHelper;
import br.dev.dias.util.HttpMethods;
import br.dev.dias.util.HttpResponseCode;
import br.dev.dias.util.HttpResponseReader;

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
    @DisplayName("Should return 200 OK for valid keyword")
    void shouldReturn200ForValidKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl";
        var expectedHttpResponseCode = HttpResponseCode.OK;
        var requestBody = "{ \"keyword\": \"validKeyword\" }";

        HttpURLConnection connection = null;
        try {
            connection = httpClientHelper.createConnection(BASE_URL + BASE_PATH, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            // When
            int responseCode = connection.getResponseCode();

            // Then
            assertThat(responseCode, is(expectedHttpResponseCode));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return response body containing id for valid keyword")
    void shouldReturnResponseBodyContainingIdForValidKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl/";
        var expectedResponseBody = "{\"id\":";
        var requestBody = "{ \"keyword\": \"validKeyword\" }";
        var url = BASE_URL+BASE_PATH;

        HttpURLConnection connection = null;
        try {
            connection = httpClientHelper.createConnection(url, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            // When
            connection.getResponseCode();
            String responseBody = httpResponseReader.readResponse(connection);

            // Then
            assertThat(responseBody, containsString(expectedResponseBody));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return Content-Type as application/json for valid keyword")
    void shouldReturnContentTypeAsApplicationJsonForValidKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl/";
        var expectedContentType = "application/json";
        var requestBody = "{ \"keyword\": \"validKeyword\" }";
        var url = BASE_URL+BASE_PATH;
        HttpURLConnection connection = null;
        try {
            connection = httpClientHelper.createConnection(url, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            // When
            connection.getResponseCode();
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat(contentType, is(expectedContentType));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request for invalid keyword")
    void shouldReturn400ForInvalidKeywordResponseCode() throws Exception {
        // Given
        var BASE_PATH = "/crawl/";
        var expectedHttpResponseCode = HttpResponseCode.BAD_REQUEST;
        var requestBody = "{ \"keyword\": \"abc\" }"; 
        HttpURLConnection connection = null;
        var url = BASE_URL + BASE_PATH;
        try {
            connection = httpClientHelper.createConnection(url, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

            // When
            int responseCode = connection.getResponseCode();

            // Then
            assertThat(responseCode, is(expectedHttpResponseCode));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return response message indicating keyword is too short")
    void shouldReturnResponseMessageForInvalidKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl/";
        var expectedResponseBody = "too short";
        var requestBody = "{ \"keyword\": \"abc\" }"; 
        var url = BASE_URL + BASE_PATH;
        HttpURLConnection connection = null;

        try {
            connection = httpClientHelper.createConnection(url, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

            // When
            connection.getResponseCode();
            String responseBody = httpResponseReader.readResponse(connection);

            // Then
            assertThat(responseBody, containsString(expectedResponseBody));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return Content-Type as application/json for invalid keyword")
    void shouldReturnContentTypeForInvalidKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl/";
        var expectedContentType = "application/json";
        var requestBody = "{ \"keyword\": \"abc\" }"; 
        var url = BASE_URL + BASE_PATH;
        HttpURLConnection connection = null;

        try {
            connection = httpClientHelper.createConnection(url, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

            // When
            connection.getResponseCode();
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat(contentType, is(expectedContentType));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request for missing keyword")
    void shouldReturn400ForMissingKeywordResponseCode() throws Exception {
        // Given
        var BASE_PATH = "/crawl/";
        var expectedHttpResponseCode = HttpResponseCode.BAD_REQUEST;
        var requestBody = "{ }";
        var url = BASE_URL + BASE_PATH;

        HttpURLConnection connection = null;
        try {
            connection = httpClientHelper.createConnection(url, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

            // When
            int responseCode = connection.getResponseCode();

            // Then
            assertThat(responseCode, is(expectedHttpResponseCode));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return response message indicating invalid keyword for missing keyword")
    void shouldReturnResponseMessageForMissingKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl";
        var expectedResponseBody = "Invalid keyword";
        var requestBody = "{ }";
        var url = BASE_URL + BASE_PATH;

        HttpURLConnection connection = null;
        try {
            connection = httpClientHelper.createConnection(url, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

            // When
            connection.getResponseCode();
            String responseBody = httpResponseReader.readResponse(connection);

            // Then
            assertThat(responseBody, containsString(expectedResponseBody));
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return Content-Type as application/json for missing keyword")
    void shouldReturnContentTypeForMissingKeyword() throws Exception {
        // Given
        var BASE_PATH = "/crawl";
        var expectedContentType = "application/json";
        var requestBody = "{ }";
        HttpURLConnection connection = null;
        var url = BASE_URL + BASE_PATH;

        try {
            connection = httpClientHelper.createConnection(url, HttpMethods.POST, requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

            // When
            connection.getResponseCode();
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat(contentType, is(expectedContentType));
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
        var BASE_PATH = "/crawl/";
        var requestBody = "{ \"keyword\": \"validKeyword\" }";
        var expectedContentType = "application/json";
        var url = BASE_URL + BASE_PATH;
        HttpURLConnection connection = null;
        try {
            connection = httpClientHelper.createConnection(url, "POST", requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);

            // When
            connection.getResponseCode();
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat(contentType, is(expectedContentType));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return 200 OK for valid ID")
    void shouldReturn200ForValidId() throws Exception {
        // Given
        var BASE_PATH = "/crawl/";
        var requestBody = "{ \"keyword\": \"validKeyword\" }";
        var url = BASE_URL + BASE_PATH;
        var expectedHttpResponseCode = HttpResponseCode.OK;
        HttpURLConnection postConnection = null;
        HttpURLConnection getConnection  = null;
        
        try {
            postConnection = httpClientHelper.createConnection(url, "POST", requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            postConnection.getResponseCode();
            String postResponseBody = httpResponseReader.readResponse(postConnection);
            String id = JsonHelper.extractFieldFromJson(postResponseBody, "id");

            // When
            url = url + id;
            getConnection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            int responseCode = getConnection.getResponseCode();

            // Then
            assertThat(responseCode, is(expectedHttpResponseCode));
        }
        finally{
            if (postConnection != null) {
                postConnection.disconnect();
            }
            if (getConnection != null) {
                getConnection.disconnect();
            }

        }
    }

    @Test
    @DisplayName("Should return Content-Type as application/json for valid ID")
    void shouldReturnContentTypeAsApplicationJsonForValidId() throws Exception {
        // Given
        var BASE_PATH = "/crawl/";
        var requestBody = "{ \"keyword\": \"validKeyword\" }";
        var expectedContentType = "application/json";
        var url = BASE_URL + BASE_PATH;

        HttpURLConnection postConnection = null;
        HttpURLConnection getConnection  = null;
        try{
            postConnection = httpClientHelper.createConnection(url, "POST", requestBody, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            postConnection.getResponseCode();
            String postResponseBody = httpResponseReader.readResponse(postConnection);
            String id = JsonHelper.extractFieldFromJson(postResponseBody, "id");

            // When
            url = url+id;
            getConnection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            String contentType = getConnection.getHeaderField("Content-Type");

            // Then
            assertThat(contentType, is(expectedContentType));
        }finally{
            if (postConnection != null) {
                postConnection.disconnect();
            }
            if (getConnection != null) {
                getConnection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request for short ID")
    void shouldReturn400ForShortIdResponseCode() throws Exception {

        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "1342";
        var expectedHttpResponseCode = HttpResponseCode.BAD_REQUEST;

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            int responseCode = connection.getResponseCode();

            // Then
            assertThat(responseCode, is(expectedHttpResponseCode));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
        
    }

    @Test
    @DisplayName("Should return response message indicating ID is too short")
    void shouldReturnResponseMessageForShortId() throws Exception {

        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "1342";
        var expectedResponseBody = "too short";

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            connection.getResponseCode();
            var responseBody = httpResponseReader.readResponse(connection);

            // Then
            assertThat(responseBody, containsString(expectedResponseBody));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
        
    }

    @Test
    @DisplayName("Should return Content-Type as application/json for short ID")
    void shouldReturnContentTypeForShortId() throws Exception {

        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "1342";
        var expectedContentType = "application/json";

        // When
        HttpURLConnection connection = null;
        
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            connection.getResponseCode();
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat(contentType, is(expectedContentType));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request for long ID")
    void shouldReturn400ForLongIdResponseCode() throws Exception {

        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "longidsended";
        var expectedHttpResponseCode = HttpResponseCode.NOT_FOUND;

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            int responseCode = connection.getResponseCode();

            // Then
            assertThat(responseCode, is(expectedHttpResponseCode));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return response message indicating ID is too long")
    void shouldReturnResponseMessageForLongId() throws Exception {
                
        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "longidsended";
        var expectedResponseBody = "too long";

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            connection.getResponseCode();
            String responseBody = httpResponseReader.readResponse(connection);

            // Then
            assertThat(responseBody, containsString(expectedResponseBody));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return Content-Type as application/json for long ID")
    void shouldReturnContentTypeForLongId() throws Exception {
        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "longidsended";
        var expectedContentType = "application/json";

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            connection.getResponseCode();
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat(contentType, is(expectedContentType));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return 404 Not Found for non-existent ID")
    void shouldReturn404ForNonExistentIdResponseCode() throws Exception {
        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "1234abcd";
        var expectedHttpResponseCode = HttpResponseCode.NOT_FOUND;

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            int responseCode = connection.getResponseCode();

            // Then
            assertThat(responseCode, is(expectedHttpResponseCode));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return response message indicating ID is not found")
    void shouldReturnResponseMessageForNonExistentId() throws Exception {

        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "1234abcd";
        var expectedResponseBody = "not found";

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            connection.getResponseCode();
            String responseBody = httpResponseReader.readResponse(connection);

            // Then
            assertThat(responseBody, containsString(expectedResponseBody));
    
        } 
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }   
    }

    @Test
    @DisplayName("Should return Content-Type as application/json for non-existent ID")
    void shouldReturnContentTypeForNonExistentId() throws Exception {
        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "1234abcd";
        var expectedContentType = "application/json";

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            connection.getResponseCode();
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat( contentType, is(expectedContentType));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return 400 Bad Request for ID with non-alphanumeric characters")
    void shouldReturn400ForNonAlphanumericIdResponseCode() throws Exception {
        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "123$abcd";
        var expectedHttpResponseCode = HttpResponseCode.BAD_REQUEST;

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            int responseCode = connection.getResponseCode();

            // Then
            assertThat(responseCode, is(expectedHttpResponseCode));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return response message indicating ID must be alphanumeric")
    void shouldReturnResponseMessageForNonAlphanumericId() throws Exception {
        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "123$abcd";
        var expectedResponseBody = "must be alphanumeric";

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
                connection.getResponseCode();
            String responseBody = httpResponseReader.readResponse(connection);

            // Then
            assertThat(responseBody, containsString(expectedResponseBody)); 
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    @DisplayName("Should return Content-Type as application/json for ID with non-alphanumeric characters")
    void shouldReturnContentTypeForNonAlphanumericId() throws Exception {
        var BASE_PATH = "/crawl/";
        var url = BASE_URL + BASE_PATH + "123$abcd";
        var expectedContentType = "application/json";

        // When
        HttpURLConnection connection = null;
        try{
            connection = httpClientHelper.createConnection(url, "GET", null, HTTP_CONNECTION_TIMEOUT, HTTP_READ_TIMEOUT);
            connection.getResponseCode();
            String contentType = connection.getHeaderField("Content-Type");

            // Then
            assertThat(contentType, is(expectedContentType));
        }
        finally{
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
