package com.axreng.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigValidator.class);

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

}
