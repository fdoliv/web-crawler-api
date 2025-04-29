package com.axreng.backend.api;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.Optional;

import com.axreng.backend.exception.KeywordValidatorException;
import com.axreng.backend.exception.SearchIDValidatorException;
import com.axreng.backend.model.Search;
import com.axreng.backend.service.CrawlerService;
import com.axreng.backend.service.SearchService;
import com.axreng.backend.util.HttpResponseCode;
import com.axreng.backend.util.KeywordValidator;
import com.axreng.backend.util.SearchIDValidator;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlController.class);

    private static final Gson GSON = new Gson();
    private static SearchService searchService; 
    private static CrawlerService crawlService; 
    private static CrawlController instance;

    private CrawlController() {

        searchService = new SearchService();
        crawlService = new CrawlerService(searchService);
    }
    public static CrawlController getInstance(){
        if (instance == null) {
            instance = new CrawlController();
        }
        return instance;
    }
    
    public static void initializeRoutes() {
        getInstance();

        get("/crawl/:id", (req, res) -> {
            res.type("application/json");

            try {
                String id = req.params("id");
                SearchIDValidator.validate(id);
                Optional<Search> optionalSearch = searchService.findSearchById(id);
                if (optionalSearch.isPresent()) {

                    CrawlStatusResponse crawlStatusResponse = new CrawlStatusResponse();
                    crawlStatusResponse.setId(optionalSearch.get().getId());
                    crawlStatusResponse.setStatus(optionalSearch.get().getStatus().getValue());
                    crawlStatusResponse.setUrls(new ArrayList<>(optionalSearch.get().getUrls()));    
                    res.status(HttpResponseCode.OK);
                    return GSON.toJson(crawlStatusResponse, CrawlStatusResponse.class);
                }
                res.status(HttpResponseCode.NOT_FOUND);
                return GSON.toJson(new CrawlErrorResponse(
                        HttpResponseCode.NOT_FOUND, 
                        "Not Found", 
                        "Search with ID " + id + " not found", 
                        req.pathInfo()
                ));
                
                
            } catch (SearchIDValidatorException sive){
                res.status(HttpResponseCode.BAD_REQUEST);

                return GSON.toJson(new CrawlErrorResponse(
                    HttpResponseCode.BAD_REQUEST, 
                    "Bad Request", 
                    sive.getMessage(), 
                    req.pathInfo()
                ));
            }
            
            catch (Exception e) {
                LOGGER.error("An unexpected error occurred", e);
                res.status(HttpResponseCode.INTERNAL_SERVER_ERROR);

                return GSON.toJson(new CrawlErrorResponse(
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

            try {
                CrawlRequest crawlRequest = GSON.fromJson(req.body(), CrawlRequest.class);
                LOGGER.info("Validating the search keyword {}", crawlRequest.getKeyword());
                KeywordValidator.validate(crawlRequest.getKeyword());
                LOGGER.info("The search keyword {} is valid.", crawlRequest.getKeyword());

                LOGGER.info("Searching for existing search with keyword {}", crawlRequest.getKeyword());
                Optional<Search> optionalSearch = searchService.findSearchByKeyword(crawlRequest.getKeyword());
                
                if (optionalSearch.isPresent()) {
                    LOGGER.info("Found existing search with ID {}", optionalSearch.get().getId());
                    res.status(HttpResponseCode.OK);
                    return GSON.toJson(new CrawlResponse(optionalSearch.get().getId()));
                }
                LOGGER.info("No existing search found. Creating a new search with keyword {}", crawlRequest.getKeyword());

                var search = new Search(crawlRequest.getKeyword());
                search = searchService.saveSearch(search);

                CrawlResponse crawlResponse = new CrawlResponse();
                crawlResponse.setId(search.getId());

                res.status(HttpResponseCode.OK);
                LOGGER.info("Search registred. Crawl response JSON: {}", crawlResponse.toJson());

                crawlService.startCrawl(search.getId());
                return crawlResponse.toJson();
            } catch (KeywordValidatorException kve) {
                LOGGER.info("Keyword validation failed. {\"message\":\"{}\"}", kve.getMessage());
                res.status(HttpResponseCode.BAD_REQUEST);

                return GSON.toJson(new CrawlErrorResponse(
                    HttpResponseCode.BAD_REQUEST, 
                    "Bad Request", 
                    kve.getMessage(), 
                    req.pathInfo()
                ));
                
            } catch(Exception e){
                LOGGER.error("An unexpected error occurred", e);
                res.status(HttpResponseCode.INTERNAL_SERVER_ERROR);

                return GSON.toJson(new CrawlErrorResponse(
                    HttpResponseCode.INTERNAL_SERVER_ERROR, 
                    "Internal Server Error", 
                    "", 
                    req.pathInfo()
                ));
            }
        });
    }
}