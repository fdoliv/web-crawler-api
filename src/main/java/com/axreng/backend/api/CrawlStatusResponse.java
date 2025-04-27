package com.axreng.backend.api;

import java.util.List;

import com.google.gson.Gson;

public class CrawlStatusResponse {
    private String id;
    private String status;
    private List<String> urls;

    public CrawlStatusResponse() {
    }

    public CrawlStatusResponse(String id, String status, List<String> urls) {
        this.id = id;
        this.status = status;
        this.urls = urls;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}