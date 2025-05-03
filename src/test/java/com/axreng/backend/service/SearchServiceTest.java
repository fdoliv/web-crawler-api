package com.axreng.backend.service;

import com.axreng.backend.exception.SearchAlreadyExistsExeption;
import com.axreng.backend.exception.SearchNotFoundException;
import com.axreng.backend.model.Search;
import com.axreng.backend.model.Status;
import com.axreng.backend.repository.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
        var keyword = "Keyword";

        
        try{
            // When
            var id = service.createSearch(keyword);
            Search result = service.findSearchById(id);

            // Then
            assertThat("The newly registered search should match the expected search object", result.getId(), is(id));
            assertThat("The keyword should match the expected keyword", result.getKeyword(), is(keyword));
            assertThat("The status should be ACTIVE", result.getStatus(), is(Status.ACTIVE));
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
            var keyword = "validKeyword";
            var id = service.createSearch(keyword);
            // When
            Search result = service.findSearchById(id);

            // Then
            assertThat("The retrieved search should match the saved search", result.getId(), is(id));
            assertThat("The keyword should match the expected keyword", result.getKeyword(), is(keyword));
            assertThat("The status should be ACTIVE", result.getStatus(), is(Status.ACTIVE));
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
            var keyword = "validKeyword";
            var id = service.createSearch(keyword);
            var newUrl = "http://dias.dev.br";

            // When
            service.addUrlToSearch(id, newUrl);
            Search result = service.findSearchById(id);

            // Then
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
            var keyword = "validKeyword";
            var id = service.createSearch(keyword);
            // When
            Search updatedSearch = service.updateSearchStatus(id);
            // Then
            assertThat("The updated search should have the status DONE", updatedSearch.getStatus(), is(Status.DONE));
        } catch (Exception e) {
            System.out.println("Search not found: " + e.getMessage());
        }
    }
}
