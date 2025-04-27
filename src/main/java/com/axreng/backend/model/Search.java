package com.axreng.backend.model;

import java.util.Set;

public class Search {
    
    String id;
    String keyword;
    Status status;     
    private Set<String> urls;

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }
}
