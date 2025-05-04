package com.axreng.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    private final String baseUrl;
    private final int maxThreads;
    
    public AppConfig() {
        LOGGER.info("Initializing application configuration...");

        this.baseUrl = System.getenv("BASE_URL");
        
        if (this.baseUrl == null || this.baseUrl.isBlank()) {
            var message = "BASE_URL is not defined in the environment variables";
            LOGGER.error(message);
            throw new IllegalStateException(message);
        }
        LOGGER.info("Base URL: {}", this.baseUrl);
        this.maxThreads = ConfigValidator.validateThreadCount(System.getenv("THREAD_COUNT"));

        LOGGER.trace("BASE_URL: {}\tTHREAD_COUNT: {}", this.baseUrl, this.maxThreads);
    }
    
   
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public int getMaxThreads() {
        return maxThreads;
    }


    public long getTaskTimeout() {
        return 60000L; 
    }

}