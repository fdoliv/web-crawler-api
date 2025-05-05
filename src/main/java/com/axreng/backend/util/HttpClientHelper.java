package com.axreng.backend.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.exception.HttpRequestFailedException;

/**
 * Utility class for handling HTTP connections and validating responses.
 * Provides methods to create HTTP connections and ensure successful responses.
 */
public class HttpClientHelper {

    private final Logger logger;

    /**
     * Default constructor that initializes the logger for this class.
     */
    public HttpClientHelper() {
        this.logger = LoggerFactory.getLogger(HttpClientHelper.class);
    }

    /**
     * Constructor that allows injecting a custom logger.
     *
     * @param logger the logger to use for logging messages
     */
    public HttpClientHelper(Logger logger) {
        this.logger = logger;
    }

    /**
     * Creates an HTTP connection with the specified parameters.
     *
     * @param url the URL to connect to
     * @param method the HTTP method (e.g., GET, POST)
     * @param body the request body (optional, can be null or blank)
     * @param connectionTimeout the connection timeout in milliseconds
     * @param readTimeout the read timeout in milliseconds
     * @return the configured HttpURLConnection
     * @throws Exception if an error occurs while creating the connection
     */
    public HttpURLConnection createConnection(String url, 
            String method, 
            String body, 
            int connectionTimeout, 
            int readTimeout) throws ConnectException, IOException {
        try {
            URL endpoint = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) endpoint.openConnection();
            httpConnection.setRequestMethod(method);
            httpConnection.setConnectTimeout(connectionTimeout);
            httpConnection.setReadTimeout(readTimeout);
            if (body != null && !body.isBlank()) {
                httpConnection.setDoOutput(true);
                try (OutputStream os = httpConnection.getOutputStream()) {
                    os.write(body.getBytes());
                    os.flush();
                }
            }
            return httpConnection;
        } catch (ConnectException e) {
            logger.error("Connection refused while connecting to URL: {}", url, e);
            throw e; // Propagate the exception for retry handling
        }
    }

    /**
     * Validates that the HTTP response has a successful status code (200 OK).
     * Throws an exception if the response code is not HTTP_OK.
     *
     * @param connection the HttpURLConnection to validate
     * @throws IOException if the response code is not HTTP_OK
     */
    public void validateSucessResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            String errorMessage = String.format("Failed to fetch content from URL: %s, Response Code: %d",
                    connection.getURL(), responseCode);
            logger.error(errorMessage);
            throw new HttpRequestFailedException(errorMessage);
        }
    }
}
