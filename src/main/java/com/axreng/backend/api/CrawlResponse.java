package com.axreng.backend.api;

public class CrawlResponse {
    private String id;

    public CrawlResponse() {
    }

    public CrawlResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
