package com.axreng.backend.api;

import com.google.gson.Gson;

public class CrawlErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;

    public CrawlErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
    
    public String toJson() {
        return new Gson().toJson(this);
    }

}
