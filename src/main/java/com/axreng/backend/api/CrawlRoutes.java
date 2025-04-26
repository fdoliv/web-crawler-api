package com.axreng.backend.api;

import static spark.Spark.*;

public class CrawlRoutes {

    public static void registerRoutes() {
        get("/crawl/:id", (req, res) ->
                "GET /crawl/" + req.params("id"));
        post("/crawl", (req, res) ->
                "POST /crawl" + System.lineSeparator() + req.body());
    }
}