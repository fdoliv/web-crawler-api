package com.axreng.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.api.CrawlController;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        LOGGER.info("Starting the application...");
        CrawlController.getInstance().initializeRoutes();

    }
}
