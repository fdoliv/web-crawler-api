package com.axreng.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AppConfig class is responsible for loading and validating application configuration
 * from environment variables. It ensures that required configurations are properly set
 * and provides access to these configurations.
 * 
 * Configuration:
 * - BASE_URL: The base URL for the application (required).
 * - THREAD_COUNT: The maximum number of threads allowed (optional, validated).
 */
public final class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    private final String baseUrl;
    private final int maxThreads;
    
    /**
     * Constructs an AppConfig instance by loading and validating configuration values
     * from environment variables. Throws an exception if required configurations are missing
     * or invalid.
     * 
     * @throws IllegalStateException if BASE_URL is not defined or invalid.
     */
    public AppConfig() {
        LOGGER.info("Initializing application configuration...");

        this.baseUrl = ConfigValidator.validateBaseUrl(System.getenv("BASE_URL"));
        LOGGER.info("Base URL: {}", this.baseUrl);

        this.maxThreads = ConfigValidator.validateThreadCount(System.getenv("THREAD_COUNT"));
        LOGGER.trace("BASE_URL: {}\tTHREAD_COUNT: {}", this.baseUrl, this.maxThreads);
    }
    
    /**
     * Retrieves the base URL of the application.
     * 
     * @return The base URL as a String.
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Retrieves the maximum number of threads allowed for the application.
     * 
     * @return The maximum thread count as an integer.
     */
    public int getMaxThreads() {
        return maxThreads;
    }
}