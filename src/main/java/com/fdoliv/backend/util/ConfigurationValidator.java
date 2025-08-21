package com.fdoliv.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ConfigValidator class provides utility methods for validating application configuration values.
 * It ensures that configuration values such as thread count and base URL meet the required criteria.
 * 
 * Validation methods:
 * - validateThreadCount: Validates and parses the thread count.
 * - validateBaseUrl: Validates the format of the base URL.
 */
public class ConfigurationValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationValidator.class);
    private static final String VALID_URL_REGEX = "https?://[\\w.-]+(:\\d+)?(/.*)?"; 
    
    public static int validateThreadCount(String threadCount) {
        int numThreads = Runtime.getRuntime().availableProcessors() + 1; 
        if (threadCount != null && !threadCount.isBlank()) {
            try {
                numThreads = Integer.parseInt(threadCount);
            } catch (NumberFormatException e) {
                var message = "THREAD_COUNT must be a valid integer";
                LOGGER.error(message);
                throw new IllegalStateException(message, e);
            }
        }   
        return numThreads;
    }

    /**
     * Validates the base URL.
     * 
     * @param baseUrl The base URL to validate.
     * @return The validated base URL.
     * @throws IllegalArgumentException if the base URL is null, blank, or invalid.
     */
    public static String validateBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("BASE_URL cannot be null or blank.");
        }
        if (!baseUrl.matches(VALID_URL_REGEX)) { // Allow both http and https
            throw new IllegalArgumentException("BASE_URL must start with http or https and be a valid URL.");
        }
        return baseUrl;
    }
}
