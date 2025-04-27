package com.axreng.backend.service;

import com.axreng.backend.model.Search;
import com.axreng.backend.model.Status;
import com.axreng.backend.repository.SearchRepository;
import com.axreng.backend.util.IDGenerator;

import java.util.List;
import java.util.Optional;

public class SearchService {

    private final SearchRepository searchRepository;

    public SearchService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public SearchService() {
        this.searchRepository = new SearchRepository();
    }

    public Search saveSearch(Search search) {
        if (search.getId() == null || search.getId().isBlank()) {
            var id = generateUniqueId();
            search.setId(id);
        }
        searchRepository.save(search);
        return search;
    }

    private String generateUniqueId() {
        var id = IDGenerator.generateAlphanumericID();
        var search = searchRepository.findById(id);
        while (search.isPresent()) {
            id = IDGenerator.generateAlphanumericID();
            search = searchRepository.findById(id);
        }
        return id;
    }

    public Optional<Search> findSearchById(String id) {
        return searchRepository.findById(id);
    }

    public List<Search> findAllSearches() {
        return searchRepository.findAll();
    }

    public Search updateSearchStatus(String id, Status status) {
        Optional<Search> optionalSearch = searchRepository.findById(id);

        
        if (optionalSearch.isEmpty()) {
            throw new IllegalArgumentException("Search with ID " + id + " not found.");
        }

        Search search = optionalSearch.get();
        Search updatedSearch = new Search(search.getId(), search.getKeyword(), status);
        searchRepository.save(updatedSearch);
        return updatedSearch;
    }

    public void deleteSearchById(String id) {
        searchRepository.deleteById(id);
    }

    public Optional<Search>  findSearchByKeyword(String keyword) {
        return searchRepository.findByKeyword(keyword);
    }
}
