package com.axreng.backend.api;

import static spark.Spark.*;

import com.axreng.backend.exception.KeywordValidatorException;
import com.axreng.backend.util.KeywordValidator;
import com.google.gson.Gson;

import javax.servlet.http.HttpServletResponse;

public class CrawlController {

    private static final Gson gson = new Gson();
    
    public static void initializeRoutes() {

        post("/crawl", (req, res) ->{
        try {
            CrawlRequest crawlRequest = gson.fromJson(req.body(), CrawlRequest.class);
            KeywordValidator.validate(crawlRequest.getKeyword());
            return "POST /crawl" + System.lineSeparator() + req.body();
        } catch (KeywordValidatorException kve) {
            res.status(HttpServletResponse.SC_BAD_REQUEST);
            res.type("application/json");

            return gson.toJson(new CrawlErrorResponse(
                400, 
                "Bad Request", 
                kve.getMessage(), 
                req.pathInfo()
            ));
        }
        
        });
    }

}