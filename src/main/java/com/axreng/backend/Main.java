package com.axreng.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.api.CrawlController;
import com.axreng.backend.service.CrawlerService;
import com.axreng.backend.service.SearchService;
import com.axreng.backend.service.ValidationService;
import com.axreng.backend.util.AppConfig;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Inicializar a aplicação
        AppConfig.getInstance();
        LOGGER.info("Starting application...");
        
        SearchService searchService = new SearchService();
        ValidationService validationService = new ValidationService();
        CrawlerService crawlerService = new CrawlerService(searchService);

        CrawlController crawlController = new CrawlController(searchService, crawlerService, validationService);
        crawlController.initializeRoutes();
    }
}
