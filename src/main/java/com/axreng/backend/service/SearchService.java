package com.axreng.backend.service;

import com.axreng.backend.model.Search;
import com.axreng.backend.model.Status;
import com.axreng.backend.repository.SearchRepository;

import java.util.List;
import java.util.Optional;

public class SearchService {

    private final SearchRepository repository;

    public SearchService(SearchRepository repository) {
        this.repository = repository;
    }

    public SearchService() {
        this.repository = new SearchRepository();
    }

    public void saveSearch(Search search) {
        repository.save(search);
    }

    public Optional<Search> findSearchById(String id) {
        return repository.findById(id);
    }

    public List<Search> findAllSearches() {
        return repository.findAll();
    }

    public Search updateSearchStatus(String id, Status status) {
        Optional<Search> optionalSearch = repository.findById(id);

        
        if (optionalSearch.isEmpty()) {
            throw new IllegalArgumentException("Search with ID " + id + " not found.");
        }

        Search search = optionalSearch.get();
        Search updatedSearch = new Search(search.getId(), search.getKeyword(), status);
        repository.save(updatedSearch);
        return updatedSearch;
    }

    public void deleteSearchById(String id) {
        repository.deleteById(id);
    }
}
