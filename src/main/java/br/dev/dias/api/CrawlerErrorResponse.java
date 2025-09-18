package br.dev.dias.api;

import com.google.gson.Gson;

public class CrawlerErrorResponse {

    private int status;
    private String error;
    private String message;
    private String path;

    public CrawlerErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
    
    public String toJson() {
        return new Gson().toJson(this);
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

}
