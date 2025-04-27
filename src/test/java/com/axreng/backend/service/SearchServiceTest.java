package com.axreng.backend.service;

import com.axreng.backend.model.Search;
import com.axreng.backend.model.Status;
import com.axreng.backend.repository.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        Search search = new Search("1", "Keyword", Status.ACTIVE);

        // When
        service.saveSearch(search);
        Optional<Search> result = service.findSearchById("1");

        // Then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(equalTo(search)));
    }


    @Test
    @DisplayName("Should find a search by ID when it exists")
    void shouldFindSearchByIdWhenExists() {
        // Given
        Search search = new Search("1", "validKeyword", Status.ACTIVE);
        service.saveSearch(search);

        // When
        Optional<Search> result = service.findSearchById("1");

        // Then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(equalTo(search)));
    }

    @Test
    @DisplayName("Should return empty when searching for a non-existent ID")
    void shouldReturnEmptyForNonExistentId() {
        // When
        Optional<Search> result = service.findSearchById("nonExistentId");

        // Then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    @DisplayName("Should update the list of URLs by adding a new URL")
    void shouldUpdateUrlsByAddingNewUrl() {
        var newUrl = "http://dias.dev.br";
        // Given
        Search search = new Search("1", "validKeyword", Status.ACTIVE);
        service.saveSearch(search);

        // When
        Search updatedSearch = new Search("1", "validKeyword", Status.ACTIVE);
        Set<String> urls = new HashSet<>(search.getUrls());
        urls.add(newUrl);
        updatedSearch.setUrls(urls);
        service.saveSearch(updatedSearch);
        Optional<Search> result = service.findSearchById("1");

        // Then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getUrls(), contains(newUrl));
    }

    @Test
    @DisplayName("Should update the status of a search from active to done")
    void shouldUpdateStatusFromActiveToDone() {
        // Given
        Search search = new Search("1", "validKeyword", Status.ACTIVE);
        service.saveSearch(search);

        // When
        Search updatedSearch = service.updateSearchStatus("1", Status.DONE);

        // Then
        assertThat(updatedSearch.getStatus(), is(Status.DONE));
        Optional<Search> result = service.findSearchById("1");
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getStatus(), is(Status.DONE));
    }
}
