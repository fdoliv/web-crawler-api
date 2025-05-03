package com.axreng.backend.api;

import static spark.Spark.*;
import com.axreng.backend.exception.SearchAlreadyExistsExeption;
import com.axreng.backend.exception.SearchNotFoundException;
import com.axreng.backend.exception.ValidationException;
import com.axreng.backend.model.Search;
import com.axreng.backend.service.CrawlerService;
import com.axreng.backend.service.SearchService;
import com.axreng.backend.service.ValidationService;
import com.axreng.backend.util.HttpResponseCode;
import com.axreng.backend.util.ResponseHelper;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlController.class);
    private static final Gson GSON = new Gson();
    private final SearchService searchService; 
    private final CrawlerService crawlerService; 
    private final ValidationService validationService;

    public CrawlController(SearchService searchService, 
            CrawlerService crawlerService, ValidationService validationService) {
        this.searchService = searchService;
        this.crawlerService = crawlerService;
        this.validationService = validationService;
    }
    
    public void initializeRoutes() {

        get("/crawl/:id", (req, res) -> {
            res.type("application/json");

            try {
                String id = req.params("id");
                LOGGER.info("Validating the search ID {}", id);
                validationService.validateSearchId(id);
                LOGGER.info("The search ID {} is valid.", id);
                res.status(HttpResponseCode.OK);
                Search search = searchService.findSearchById(id);
                LOGGER.info("Search with ID {} found. Status: {}", id, search.getStatus());
                return ResponseHelper.createCrawlStatusResponse(search);
                
            } catch (ValidationException ve){
                res.status(HttpResponseCode.BAD_REQUEST);

                return GSON.toJson(new CrawlerErrorResponse(
                    HttpResponseCode.BAD_REQUEST, 
                    "Bad Request", 
                    ve.getMessage(), 
                    req.pathInfo()
                ));
            } catch (SearchNotFoundException snfe) {
                LOGGER.info("Search with ID {} not found. {}", req.params("id"), snfe.getMessage());
                res.status(HttpResponseCode.NOT_FOUND);

                return ResponseHelper.createErrorResponse(
                    HttpResponseCode.NOT_FOUND, 
                    "Not Found", 
                    snfe.getMessage(), 
                    req.pathInfo()
                ); 
            }
            
            catch (Exception e) {
                LOGGER.error("An unexpected error occurred", e);
                res.status(HttpResponseCode.INTERNAL_SERVER_ERROR);

                return GSON.toJson(new CrawlerErrorResponse(
                    HttpResponseCode.INTERNAL_SERVER_ERROR, 
                    "Internal Server Error", 
                    "", 
                    req.pathInfo()
                ));
            }
        });
  
        post("/crawl", (req, res) ->{

            res.type("application/json");
            LOGGER.info("Received request to crawl with body: {}", req.body());
            CrawlerRequest crawlRequest = GSON.fromJson(req.body(), CrawlerRequest.class);
            LOGGER.debug("Crawl request parsed: {}", crawlRequest.toString());
            try {
                LOGGER.info("No existing search found. Creating a new search with keyword {}", crawlRequest.getKeyword());

                LOGGER.info("Validating the search keyword {}", crawlRequest.getKeyword());
                validationService.validateKeyword(crawlRequest.getKeyword());

                LOGGER.info("The search keyword {} is valid.", crawlRequest.getKeyword());
                

                String searchId = searchService.createSearch(crawlRequest.getKeyword());

                LOGGER.info("Search created with ID {}", searchId);
                LOGGER.info("Starting crawl for search with ID {}", searchId);
                crawlerService.startCrawl(searchId);

                res.status(HttpResponseCode.OK);
                return ResponseHelper.createSuccessResponse(searchId);
            } catch (ValidationException ve) {
                LOGGER.info("Keyword validation failed. {\"message\":\"{}\"}", ve.getMessage());
                res.status(HttpResponseCode.BAD_REQUEST);
                return ResponseHelper.createErrorResponse(HttpResponseCode.BAD_REQUEST, "Bad Request", ve.getMessage(), req.pathInfo());
                
            } catch (SearchAlreadyExistsExeption safe){
                
                LOGGER.info("Retrieving existing search data for keyword: {}", crawlRequest.getKeyword());
                Search search = searchService.findSearchByKeyword(crawlRequest.getKeyword());                
                res.status(HttpResponseCode.OK);
                return ResponseHelper.createSuccessResponse(search.getId());
            } 
            catch(Exception e){
                LOGGER.error("An unexpected error occurred", e);
                res.status(HttpResponseCode.INTERNAL_SERVER_ERROR);

                return ResponseHelper.createErrorResponse(
                    HttpResponseCode.INTERNAL_SERVER_ERROR, 
                    "Internal Server Error", 
                    "", 
                    req.pathInfo()
                ); 
            }
        });
    }
}