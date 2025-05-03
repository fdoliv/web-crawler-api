package com.axreng.backend.service;

import com.axreng.backend.exception.SearchAlreadyExistsExeption;
import com.axreng.backend.exception.SearchNotFoundException;
import com.axreng.backend.model.Search;
import com.axreng.backend.model.Status;
import com.axreng.backend.repository.SearchRepository;
import com.axreng.backend.util.IDGenerator;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchService {
        private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);


    private final SearchRepository searchRepository;

    public SearchService(SearchRepository searchRepository) {
        LOGGER.info("Initializing SearchService with provided SearchRepository");
        this.searchRepository = searchRepository;
    }

    public SearchService() {
        LOGGER.info("Initializing SearchService with default SearchRepository");
        this.searchRepository = new SearchRepository();
    }

    public Search saveSearch(Search search) throws SearchAlreadyExistsExeption {
        LOGGER.debug("Attempting to save search with keyword: {}", search.getKeyword());

        try {
            LOGGER.debug("Checking if search with keyword '{}' already exists", search.getKeyword());
            findSearchByKeyword(search.getKeyword());
            var message = String.format("Search with keyword %s already exists.", search.getKeyword());
            LOGGER.warn(message);
            throw new SearchAlreadyExistsExeption(message);
        } catch (SearchNotFoundException snfe) {
            LOGGER.debug("Search with keyword '{}' does not exist. Proceeding to save.", search.getKeyword());
            if (search.getId() == null || search.getId().isBlank()) {
                LOGGER.debug("Search ID is null or blank. Generating a unique ID.");
                var id = generateUniqueId();
                search.setId(id);
                LOGGER.debug("Generated unique ID: {}", id);
            }
            searchRepository.save(search);
            LOGGER.info("Search with keyword '{}' and ID '{}' saved successfully.", search.getKeyword(), search.getId());
            return search;
        }
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

    public Search findSearchById(String id) throws SearchNotFoundException {
        System.out.println("Attempting to find search with ID: " + id);
        var errorMessage = String.format("Search with ID %s not found", id);
        Search search = searchRepository.findById(id).orElseThrow(() -> {
            
            return new SearchNotFoundException(errorMessage);
        });
        return search;
    }

    public List<Search> findAllSearches() {
        return searchRepository.findAll();
    }

    public Search updateSearchStatus(String id) {
        Optional<Search> optionalSearch = searchRepository.findById(id);

        
        if (optionalSearch.isEmpty()) {
            throw new IllegalArgumentException("Search with ID " + id + " not found.");
        }

        Search search = optionalSearch.get();
        search.setStatus(Status.DONE);
        
        searchRepository.save(search);
        return search;
    }

    public void deleteSearchById(String id) {
        searchRepository.deleteById(id);
    }

    public Search  findSearchByKeyword(String keyword) throws SearchNotFoundException {
        var errorMessage = String.format("Search with keyword {} not found", keyword );
        Search search = searchRepository.findByKeyword(keyword).orElseThrow(() -> new SearchNotFoundException(errorMessage)); 
        return search;
    }

    public void addUrlToSearch(String id, String url) {
        Optional<Search> optionalSearch = searchRepository.findById(id);
        if (optionalSearch.isPresent()) {
            Search search = optionalSearch.get();
            search.getUrls().add(url);
            searchRepository.save(search);
        } else {
            throw new IllegalArgumentException("Search with ID " + id + " not found.");
        }
    }
}
