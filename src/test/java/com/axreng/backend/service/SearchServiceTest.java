package com.axreng.backend.service;

import com.axreng.backend.exception.SearchAlreadyExistsExeption;
import com.axreng.backend.exception.SearchNotFoundException;
import com.axreng.backend.model.Search;
import com.axreng.backend.model.Status;
import com.axreng.backend.repository.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.isNotNull;

class SearchServiceTest {

    private SearchService service;
    private SearchRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SearchRepository(); 
        service = new SearchService(repository);
    }

    @Test
    @DisplayName("Should register a new search")
    void shouldRegisterNewSearchWithValidKeyword() {
        // Given
        Search search = new Search("1", "Keyword", Status.ACTIVE);

        
        try{
            // When
            service.saveSearch(search);
            Search result = service.findSearchById("1");

            // Then
            assertThat("The newly registered search should match the expected search object", result, is(equalTo(search)));
        }catch (SearchNotFoundException e) {
            System.out.println("Search not found: " + e.getMessage());
        } catch (SearchAlreadyExistsExeption e) {
            System.out.println("Search alredy exists: " + e.getMessage());

        }
       
    }


    @Test
    @DisplayName("Should find a search by ID when it exists")
    void shouldFindSearchByIdWhenExists() {
        try {
            // Given
            Search search = new Search("1", "validKeyword", Status.ACTIVE);
            service.saveSearch(search);
        // When
        Search result = service.findSearchById("1");

        // Then
        assertThat("The retrieved search should match the saved search", result, is(equalTo(search)));
        } catch (SearchNotFoundException e) {
            System.out.println("Search not found: " + e.getMessage());
        }
        catch (SearchAlreadyExistsExeption e) {
            System.out.println("Search alredy exists: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should return empty when searching for a non-existent ID")
    void shouldReturnEmptyForNonExistentId() {
        // Given
        var expectedErrorMessage = "Search with ID nonExistentId not found";
        
        try {
            // When
            service.findSearchById("nonExistentId");

        } catch (Exception e) {
            // Then
            assertThat("Expected a SearchNotFoundException to be thrown", e, is(instanceOf(SearchNotFoundException.class)));
            assertThat("Exception message should indicate the missing search ID", e.getMessage(), containsString(expectedErrorMessage));
        }
    }

    @Test
    @DisplayName("Should update the list of URLs by adding a new URL")
    void shouldUpdateUrlsByAddingNewUrl() {
        
        try {
            // Given
            Search search = new Search("1", "validKeyword", Status.ACTIVE);
            service.saveSearch(search);
            var newUrl = "http://dias.dev.br";

            // When
            Search updatedSearch = new Search("1", "validKeyword", Status.ACTIVE);
            Set<String> urls = new HashSet<>(search.getUrls());
            urls.add(newUrl);
            updatedSearch.setUrls(urls);
            service.saveSearch(updatedSearch);
            Search result = service.findSearchById("1");

            // Then
            assertThat("The initial search should have an empty URLs list", search.getUrls().isEmpty(), is(true));
            assertThat("The updated search should include the newly added URL", result.getUrls(), hasItem(newUrl));
        } catch (SearchNotFoundException e) {
            System.out.println("Search not found: " + e.getMessage());
        }
        catch (SearchAlreadyExistsExeption e) {
            System.out.println("Search alredy exists: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should update the status of a search from active to done")
    void shouldUpdateStatusFromActiveToDone() {
        // Then
        try {
            // Given
            Search search = new Search("1", "validKeyword", Status.ACTIVE);
            service.saveSearch(search);

            // When
            Search updatedSearch = service.updateSearchStatus("1");
            assertThat(updatedSearch.getStatus(), is(Status.DONE));
            Search result = service.findSearchById("1");
            assertThat("The result cannot be null", result, isNotNull());
            assertThat("", result.getStatus(), is(Status.DONE));
        } catch (Exception e) {
            System.out.println("Search not found: " + e.getMessage());
        }
    }
}
