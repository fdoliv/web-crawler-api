package com.fdoliv.backend.util;

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
public class ApplicationConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfiguration.class);

    private final String baseUrl;
    private final int maxThreads;
    private final int minThreads;
    
    /**
     * Constructs an AppConfig instance by loading and validating configuration values
     * from environment variables. Throws an exception if required configurations are missing
     * or invalid.
     * 
     * @throws IllegalStateException if BASE_URL is not defined or invalid.
     */
    public ApplicationConfiguration() {
        LOGGER.info("Initializing application configuration...");

        String rawBaseUrl = ConfigurationValidator.validateBaseUrl(System.getenv("BASE_URL"));
        this.baseUrl = rawBaseUrl.endsWith("/") ? rawBaseUrl.substring(0, rawBaseUrl.length() - 1) : rawBaseUrl;
        LOGGER.info("Base URL: {}", this.baseUrl);

        this.maxThreads = ConfigurationValidator.validateThreadCount(System.getenv("THREAD_COUNT"));
        LOGGER.trace("BASE_URL: {}\tTHREAD_COUNT: {}", this.baseUrl, this.maxThreads);
        this.minThreads = Math.max(1, this.maxThreads / 2); 
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

    /**
     * Retrieves the minimum number of threads allowed for the application.
     * 
     * @return The minimum thread count as an integer.
     */
    public int getMinThreads() {
        return minThreads;
    }
}