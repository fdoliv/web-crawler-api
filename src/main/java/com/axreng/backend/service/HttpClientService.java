package com.axreng.backend.service;

import java.net.HttpURLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.util.HttpClientHelper;
import com.axreng.backend.util.HttpResponseReader;

public class HttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);

    private final HttpClientHelper httpClientHelper;
    private final HttpResponseReader httpResponseReader;
    public HttpClientService() {
        LOGGER.info("Initializing HttpClientService");
        this.httpClientHelper = new HttpClientHelper();
        this.httpResponseReader = new HttpResponseReader();
    }


    public HttpClientService(HttpClientHelper httpClientHelper, HttpResponseReader httpResponseReader) {
        LOGGER.info("Initializing HttpClientService");
        this.httpClientHelper = httpClientHelper;
        this.httpResponseReader = httpResponseReader;
    }

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
