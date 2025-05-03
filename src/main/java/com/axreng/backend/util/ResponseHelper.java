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

public class ResponseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHelper.class);
    private static final Gson GSON = new Gson();
    
    public static String createErrorResponse(int statusCode, String error,String message, String path) {
        CrawlerErrorResponse errorResponse = new CrawlerErrorResponse(statusCode, error, message, path);
        return GSON.toJson(errorResponse, CrawlerErrorResponse.class);
    }

    public static String createSuccessResponse(String crawlerRequestId) {
        CrawlerSucessResponse successResponse = new CrawlerSucessResponse(crawlerRequestId);
        return GSON.toJson(successResponse, CrawlerSucessResponse.class);
    }

    public static String createCrawlStatusResponse(Search searchStatus) {
        LOGGER.trace("Creating CrawlerStatusResponse for Search ID: {}", searchStatus.getId());
        
        CrawlerStatusResponse crawlStatusResponse = new CrawlerStatusResponse();
        crawlStatusResponse.setId(searchStatus.getId());
        crawlStatusResponse.setStatus(searchStatus.getStatus().getValue());
        
        List<String> urls = new ArrayList<>(searchStatus.getUrls());
        crawlStatusResponse.setUrls(urls);
        
        LOGGER.trace("CrawlerStatusResponse created with ID: {}, Status: {}, URLs: {}", 
                     crawlStatusResponse.getId(), 
                     crawlStatusResponse.getStatus(), 
                     crawlStatusResponse.getUrls());
        
        return GSON.toJson(crawlStatusResponse, CrawlerStatusResponse.class);
    }
}
