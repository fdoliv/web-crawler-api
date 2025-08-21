package com.fdoliv.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fdoliv.backend.api.CrawlController;
import com.fdoliv.backend.service.CrawlerService;
import com.fdoliv.backend.service.HttpClientService;
import com.fdoliv.backend.service.KeywordSearchService;
import com.fdoliv.backend.service.LinkExtractorService;
import com.fdoliv.backend.service.SearchService;
import com.fdoliv.backend.service.ValidationService;
import com.fdoliv.backend.util.ApplicationConfiguration;

/**
 * Entry point for the application.
 * Initializes services, controllers, and routes to start the crawler application.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * Main method to start the application.
     * Initializes configuration, services, and controllers, and sets up routes.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try{
            ApplicationConfiguration appConfig = new ApplicationConfiguration();
            LOGGER.info("Starting application...");
            
            SearchService searchService = new SearchService();
            ValidationService validationService = new ValidationService();
            KeywordSearchService keywordSearchService = new KeywordSearchService();
            LinkExtractorService linkExtractorService = new LinkExtractorService();
            HttpClientService httpClientService = new HttpClientService();
            
            CrawlerService crawlerService = new CrawlerService(searchService, keywordSearchService, linkExtractorService, httpClientService, appConfig);
    
            CrawlController crawlController = new CrawlController(searchService, crawlerService, validationService);
            crawlController.initializeRoutes();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutting down application...");
                spark.Spark.stop();
                crawlerService.shutdown();
            }));
        } catch(IllegalArgumentException iae){
            LOGGER.error("Error to start application: {}", iae.getMessage());
        }
        
    }
}
