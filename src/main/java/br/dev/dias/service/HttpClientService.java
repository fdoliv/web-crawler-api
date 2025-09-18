package br.dev.dias.service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.dev.dias.exception.FailedFetchContentException;
import br.dev.dias.exception.HttpRequestFailedException;
import br.dev.dias.util.HttpClientHelper;
import br.dev.dias.util.HttpResponseReader;

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
     * @throws FailedFetchContentException if the content could not be fetched due to connection or I/O errors
     * @throws HttpRequestFailedException if the HTTP request is not successful (response code is not 200)
     */
    public String fetchContent(String url) throws FailedFetchContentException, HttpRequestFailedException {
        LOGGER.debug("Fetching content from URL: {}", url);
        HttpURLConnection httpConnection = null;
        try {
            httpConnection = httpClientHelper.createConnection(url, "GET", null, 5000, 5000);
            httpClientHelper.validateSucessResponse(httpConnection);
            return httpResponseReader.readResponse(httpConnection);
        } catch (HttpRequestFailedException hrfe){
            throw hrfe;
        } catch (ConnectException ce){
            LOGGER.error("Connection error occurred while fetching content from URL: {}", url, ce);
            throw new FailedFetchContentException(
                String.format("Connection error occurred while fetching content from URL: %s", url), ce);
        }
        catch (IOException ioe){
            LOGGER.error("I/O error occurred while fetching content from URL: {}", url, ioe);
            throw new FailedFetchContentException(
                String.format("I/O error occurred while fetching content from URL: %s", url), ioe);
        } 
        finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }
}
