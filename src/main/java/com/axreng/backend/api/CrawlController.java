package com.axreng.backend.api;

import static spark.Spark.*;

import java.util.Optional;

import com.axreng.backend.exception.KeywordValidatorException;
import com.axreng.backend.model.Search;
import com.axreng.backend.service.SearchService;
import com.axreng.backend.util.KeywordValidator;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlController.class);

    private static final Gson GSON = new Gson();
    private static SearchService searchService; 
    private static CrawlController instance;

    private CrawlController() {

        searchService = new SearchService();
    }

    public static CrawlController getInstance() {
        if (instance == null) {
            instance = new CrawlController();
        }
        return instance;
    }
    
    public void initializeRoutes() {

        post("/crawl", (req, res) ->{

            res.type("application/json");

            try {
                CrawlRequest crawlRequest = GSON.fromJson(req.body(), CrawlRequest.class);
                KeywordValidator.validate(crawlRequest.getKeyword());

                Optional<Search> optionalSearch = searchService.findSearchByKeyword(crawlRequest.getKeyword());
                
                if (optionalSearch.isPresent()) {
                    res.status(HttpServletResponse.SC_OK);
                    return GSON.toJson(new CrawlResponse(optionalSearch.get().getId()));
                }

                var search = new Search(crawlRequest.getKeyword());
                search = searchService.saveSearch(search);

                CrawlResponse crawlResponse = new CrawlResponse();
                crawlResponse.setId(search.getId());

                res.status(HttpServletResponse.SC_OK);
                
                return crawlResponse.toJson();
            } catch (KeywordValidatorException kve) {

                res.status(HttpServletResponse.SC_BAD_REQUEST);

                return GSON.toJson(new CrawlErrorResponse(
                    HttpServletResponse.SC_BAD_REQUEST, 
                    "Bad Request", 
                    kve.getMessage(), 
                    req.pathInfo()
                ));
                
            } catch(Exception e){
                LOGGER.error("An unexpected error occurred", e);
                res.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                return GSON.toJson(new CrawlErrorResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Internal Server Error", 
                    "", 
                    req.pathInfo()
                ));
            }
        });
    }

}