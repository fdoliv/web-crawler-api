package com.axreng.backend.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Search {
    
    String id;
    String keyword;
    Status status;     
    private Set<String> urls;

    public Search(String id, String keyword, Status status) {
        this.id = id;
        this.keyword = keyword;
        this.status = status;
        this.urls = new HashSet<>();
    }

    public Search(String id) {
        this.id = id;
    }

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }

    public String getId() {
        return id;
    }

    public String getKeyword() {
        return keyword;
    }

    public Status getStatus() {
        return status;
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Search search = (Search) o;
        return Objects.equals(id, search.id) &&
               Objects.equals(keyword, search.keyword) &&
               Objects.equals(status, search.status);
    }
}
