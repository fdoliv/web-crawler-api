package com.axreng.backend.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.api.CrawlerErrorResponse;
import com.axreng.backend.api.CrawlerStatusResponse;
import com.axreng.backend.api.CrawlerSucessResponse;
import com.axreng.backend.model.Search;
import com.google.gson.Gson;

/**
 * Utility class for creating JSON responses for the crawler API.
 * Provides methods to generate error, success, and status responses.
 */
public class ResponseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHelper.class);
    private static final Gson GSON = new Gson();

    /**
     * Creates a JSON-formatted error response.
     *
     * @param statusCode the HTTP status code
     * @param error the error type or title
     * @param message the error message
     * @param path the API path where the error occurred
     * @return a JSON string representing the error response
     */
    public static String createErrorResponse(int statusCode, String error, String message, String path) {
        CrawlerErrorResponse errorResponse = new CrawlerErrorResponse(statusCode, error, message, path);
        return GSON.toJson(errorResponse, CrawlerErrorResponse.class);
    }

    /**
     * Creates a JSON-formatted success response.
     *
     * @param crawlerRequestId the ID of the crawler request
     * @return a JSON string representing the success response
     */
    public static String createSuccessResponse(String crawlerRequestId) {
        CrawlerSucessResponse successResponse = new CrawlerSucessResponse(crawlerRequestId);
        return GSON.toJson(successResponse, CrawlerSucessResponse.class);
    }

    /**
     * Creates a JSON-formatted crawl status response.
     *
     * @param searchStatus the Search object containing the crawl status and URLs
     * @return a JSON string representing the crawl status response
     */
    public static String createCrawlStatusResponse(Search searchStatus) {
        LOGGER.trace("Creating CrawlerStatusResponse for Search ID: {}", searchStatus.getId());
        
        CrawlerStatusResponse crawlStatusResponse = new CrawlerStatusResponse();
        crawlStatusResponse.setId(searchStatus.getId());
        crawlStatusResponse.setStatus(searchStatus.getStatus().getValue());
        
        List<String> urls = new ArrayList<>();
        if (searchStatus.getUrls() != null) {
            try {
                urls.addAll(searchStatus.getUrls());
            } catch (Exception e) {
                LOGGER.error("Error while copying URLs from searchStatus: {}", e.getMessage(), e);
            }
        }
        crawlStatusResponse.setUrls(urls);
        
        LOGGER.trace("CrawlerStatusResponse created with ID: {}, Status: {}, URLs: {}", 
                     crawlStatusResponse.getId(), 
                     crawlStatusResponse.getStatus(), 
                     crawlStatusResponse.getUrls());
        
        return GSON.toJson(crawlStatusResponse, CrawlerStatusResponse.class);
    }
}
