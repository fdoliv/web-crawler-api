package com.axreng.backend.api;

import com.google.gson.Gson;

public class CrawlerSucessResponse {
    private String id;

    public CrawlerSucessResponse() {
    }

    public CrawlerSucessResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
