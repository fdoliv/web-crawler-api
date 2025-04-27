package com.axreng.backend.repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.axreng.backend.model.Search;

public class SearchRepository {
    private final ConcurrentHashMap<String, Search> database = new ConcurrentHashMap<>();
    
    public void save(Search search) {
        Objects.requireNonNull(search, "Search object cannot be null");
        String id = Objects.requireNonNull(search.getId(), "Search ID cannot be null");
        database.put(id, search);
    }
    
    public Optional<Search> findById(String id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public boolean deleteById(String id) {
        return database.remove(id) != null;
    }
    
    public List<Search> findAll() {
        return new ArrayList<>(database.values());
    }
       
    public long count() {
        return database.mappingCount();
    }
    
    public boolean existsById(String id) {
        return database.containsKey(id);
    }
    
    public void saveAll(Map<String, Search> entries) {
        entries.forEach((id, search) -> save(search));
    }
    
}