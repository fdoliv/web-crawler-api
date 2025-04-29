package com.axreng.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);
    private static AppConfig instance;

    private final String baseUrl;
    private final int threadCount;
    
    private AppConfig() {
        LOGGER.info("Initializing application configuration...");

        this.baseUrl = "http://127.0.0.1:8080/"; // TODO: change to this = System.getenv("BASE_URL");
        
        if (this.baseUrl == null || this.baseUrl.isBlank()) {
            var message = "BASE_URL is not defined in the environment variables";
            LOGGER.error(message);
            throw new IllegalStateException(message);
        }
        LOGGER.info("Base URL: {}", this.baseUrl);
        String threadCountStr = System.getenv("THREAD_COUNT");
        if (threadCountStr != null && !threadCountStr.isBlank()) {
            try {
                this.threadCount = Integer.parseInt(threadCountStr);
            } catch (NumberFormatException e) {
                var message = "THREAD_COUNT must be a valid integer";
                LOGGER.error(message);
                throw new IllegalStateException(message, e);
            }
        } else {
            this.threadCount = Runtime.getRuntime().availableProcessors() + 1; 
        }
        LOGGER.trace("BASE_URL: {}\tTHREAD_COUNT: {}", this.baseUrl, this.threadCount);
    }
    
    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public int getThreadCount() {
        return threadCount;
    }
}