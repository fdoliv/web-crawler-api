package com.axreng.backend.service;

import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.util.HttpClientHelper;
import com.axreng.backend.util.HttpResponseReader;

/**
 * Service class for handling HTTP client operations.
 * Provides methods to fetch content from a given URL using HTTP GET requests.
 */
public class HttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);

    private final HttpClientHelper httpClientHelper;
    private final HttpResponseReader httpResponseReader;

    /**
     * Default constructor that initializes the service with default helper and reader instances.
     */
    public HttpClientService() {
        LOGGER.info("Initializing HttpClientService");
        this.httpClientHelper = new HttpClientHelper();
        this.httpResponseReader = new HttpResponseReader();
    }

    /**
     * Constructor that allows injecting custom HttpClientHelper and HttpResponseReader instances.
     *
     * @param httpClientHelper the helper for creating and managing HTTP connections
     * @param httpResponseReader the reader for processing HTTP responses
     */
    public HttpClientService(HttpClientHelper httpClientHelper, HttpResponseReader httpResponseReader) {
        LOGGER.info("Initializing HttpClientService");
        this.httpClientHelper = httpClientHelper;
        this.httpResponseReader = httpResponseReader;
    }

    /**
     * Fetches the content from the specified URL using an HTTP GET request.
     *
     * @param url the URL to fetch content from
     * @return the content retrieved from the URL as a String
     * @throws Exception if an error occurs during the HTTP request or response processing
     */
    public String fetchContent(String url) throws Exception {
        LOGGER.debug("Fetching content from URL: {}", url);
        HttpURLConnection httpConnection = null;
        try {
            httpConnection = httpClientHelper.createConnection(url, "GET", null, 5000, 5000);
            httpClientHelper.validateSucessResponse(httpConnection);
            return httpResponseReader.readResponse(httpConnection);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }
}
