package br.dev.dias.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.dev.dias.exception.SearchAlreadyExistsExeption;
import br.dev.dias.exception.SearchNotFoundException;
import br.dev.dias.model.Search;
import br.dev.dias.model.Status;
import br.dev.dias.repository.SearchRepository;
import br.dev.dias.util.IDGenerator;

/**
 * Service for managing search operations, including creating, retrieving, updating, 
 * and adding URLs to searches.
 */
public class SearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

    private final SearchRepository searchRepository;

    /**
     * Constructs a SearchService with the specified SearchRepository.
     *
     * @param searchRepository the repository for managing search data
     */
    public SearchService(SearchRepository searchRepository) {
        LOGGER.info("Initializing SearchService with provided SearchRepository");
        this.searchRepository = searchRepository;
    }

    /**
     * Constructs a SearchService with a default SearchRepository.
     */
    public SearchService() {
        LOGGER.info("Initializing SearchService with default SearchRepository");
        this.searchRepository = new SearchRepository();
    }

    /**
     * Creates a new search with the specified keyword.
     *
     * @param keyword the keyword for the search
     * @return the ID of the created search
     * @throws SearchAlreadyExistsExeption if a search with the same keyword already exists
     */
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

    /**
     * Finds a search by its ID.
     *
     * @param id the ID of the search
     * @return the Search object
     * @throws SearchNotFoundException if no search with the specified ID is found
     */
    public Search findSearchById(String id) throws SearchNotFoundException {
        LOGGER.debug("Attempting to find search with ID: {}", id);
        var errorMessage = String.format("Search with ID %s not found", id);
        Search search = searchRepository.findById(id).orElseThrow(() -> {

            return new SearchNotFoundException(errorMessage);
        });
        return search;
    }

    /**
     * Retrieves all searches.
     *
     * @return a list of all Search objects
     */
    public List<Search> findAllSearches() {
        return searchRepository.findAll();
    }

    /**
     * Updates the status of a search to DONE.
     *
     * @param id the ID of the search to update
     * @return the updated Search object
     * @throws SearchNotFoundException if no search with the specified ID is found
     */
    public Search updateSearchStatus(String id) throws SearchNotFoundException {
        Search search = findSearchById(id);
        search.setStatus(Status.DONE);
        searchRepository.save(search);
        return search;
    }

    /**
     * Finds a search by its keyword.
     *
     * @param keyword the keyword of the search
     * @return the Search object
     * @throws SearchNotFoundException if no search with the specified keyword is found
     */
    public Search findSearchByKeyword(String keyword) throws SearchNotFoundException {
        var errorMessage = String.format("Search with keyword {} not found", keyword);
        Search search = searchRepository.findByKeyword(keyword)
                .orElseThrow(() -> new SearchNotFoundException(errorMessage));
        return search;
    }

    /**
     * Adds a URL to the search with the specified ID.
     *
     * @param id the ID of the search
     * @param url the URL to add
     * @throws SearchNotFoundException if no search with the specified ID is found
     */
    public void addUrlToSearch(String id, String url) throws SearchNotFoundException {
        var errorMessage = String.format("Search with ID %s not found", id);
        Search search = searchRepository.findById(id).orElseThrow(()-> {
            return new SearchNotFoundException(errorMessage);
        });

        search.getUrls().add(url);
        searchRepository.save(search);

    }
}
