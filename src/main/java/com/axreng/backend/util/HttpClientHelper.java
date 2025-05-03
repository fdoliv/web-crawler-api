package com.axreng.backend.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.exception.FailedFetchContentException;

public class HttpClientHelper {

    private final Logger logger;

    public HttpClientHelper() {
        this.logger = LoggerFactory.getLogger(HttpClientHelper.class);
    }
    public HttpClientHelper (Logger logger) {
        this.logger = logger;
    }

    public HttpURLConnection createConnection(String url, 
            String method, 
            String body, 
            int connectionTimeout, 
            int readTimeout) throws Exception {
        
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
    }

    public void validateSucessResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            String errorMessage = String.format("Failed to fetch content from URL: %s, Response Code: %d",
                    connection.getURL(), responseCode);
            logger.error(errorMessage);
            throw new FailedFetchContentException(errorMessage);
        }
    }

}
