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

    public String createSearch(String keyword) throws SearchAlreadyExistsExeption {

        try {
            LOGGER.debug("Checking if search with keyword '{}' already exists", keyword);
            findSearchByKeyword(keyword);
            var message = String.format("Search with keyword %s already exists.", keyword);
            LOGGER.warn(message);
            throw new SearchAlreadyExistsExeption(message);
        } catch (SearchNotFoundException snfe) {
            LOGGER.debug("Search with keyword '{}' does not exist. Proceeding to save.", keyword);
            String id = IDGenerator.generateUniqueId(searchRepository);
            Search search = new Search(id, keyword, Status.ACTIVE);
            searchRepository.save(search);
            LOGGER.info("Search with keyword '{}' and ID '{}' saved successfully.", keyword,
                    id);
            return id;
        }
        
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

    public Search findSearchByKeyword(String keyword) throws SearchNotFoundException {
        var errorMessage = String.format("Search with keyword {} not found", keyword);
        Search search = searchRepository.findByKeyword(keyword)
                .orElseThrow(() -> new SearchNotFoundException(errorMessage));
        return search;
    }

    public void addUrlToSearch(String id, String url) throws SearchNotFoundException {
        var errorMessage = String.format("Search with ID %s not found", id);
        Search search = searchRepository.findById(id).orElseThrow(()-> {
            return new SearchNotFoundException(errorMessage);
        });

        search.getUrls().add(url);
        searchRepository.save(search);

    }
}
