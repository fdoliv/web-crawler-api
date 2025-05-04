package com.axreng.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.api.CrawlController;
import com.axreng.backend.service.CrawlerService;
import com.axreng.backend.service.HttpClientService;
import com.axreng.backend.service.KeywordSearchService;
import com.axreng.backend.service.LinkExtractorService;
import com.axreng.backend.service.SearchService;
import com.axreng.backend.service.ValidationService;
import com.axreng.backend.util.AppConfig;

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
            AppConfig appConfig = new AppConfig();
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
