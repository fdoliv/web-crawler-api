package br.dev.dias.api;

import java.util.List;

import com.google.gson.Gson;

public class CrawlerStatusResponse {
    private String id;
    private String status;
    private List<String> urls;

    public CrawlerStatusResponse() {
    }

    public CrawlerStatusResponse(String id, String status, List<String> urls) {
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

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getUrls() {
        return urls;
    }
}