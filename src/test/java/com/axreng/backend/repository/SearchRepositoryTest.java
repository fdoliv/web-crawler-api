package com.axreng.backend.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fdoliv.backend.model.Search;
import com.fdoliv.backend.model.Status;
import com.fdoliv.backend.repository.SearchRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SearchRepositoryTest {

    private SearchRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SearchRepository(); 
    }

    @Test
    @DisplayName("Should save a search and retrieve it by ID")
    void shouldSaveAndFindById() {
        // Given
        Search search = new Search("1", "keyword1", Status.ACTIVE);

        // When
        repository.save(search);
        Optional<Search> result = repository.findById("1");

        // Then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(equalTo(search)));
    }

    @Test
    @DisplayName("Should throw NullPointerException when saving a null search")
    void shouldThrowExceptionWhenSavingNullSearch() {
        // Given
        Search search = null;

        // When 
        NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(
            NullPointerException.class,
            () -> repository.save(search)
        );

        // Then
        assertThat(exception.getMessage(), is("Search object cannot be null"));
    }

    @Test
    @DisplayName("Should throw NullPointerException when saving a search with null id")
    void shouldThrowExceptionWhenSavingSearchWithNullId() {
        // Given
        Search search = new Search(null, "keyword1", Status.ACTIVE);
        // When 
        NullPointerException exception = org.junit.jupiter.api.Assertions.assertThrows(
            NullPointerException.class,
            () -> repository.save(search)
        );

        // Then
        assertThat(exception.getMessage(), is("Search ID cannot be null"));
    }

    @Test
    @DisplayName("Should return all saved searches")
    void shouldReturnAllSearches() {
        // Given
        Search search1 = new Search("1", "keyword1", Status.ACTIVE);
        Search search2 = new Search("2", "keyword2", Status.DONE);

        // When
        repository.save(search1);
        repository.save(search2);
        List<Search> allSearches = repository.findAll();

        // Then
        assertThat(allSearches, containsInAnyOrder(search1, search2));
    }

    @Test
    @DisplayName("Should update an existing search")
    void shouldUpdateExistingSearch() {
        // Given
        Search search = new Search("1", "keyword1", Status.ACTIVE);
        repository.save(search);

        // When
        Search updatedSearch = new Search("1", "keyword1", Status.DONE);
        repository.save(updatedSearch);
        Optional<Search> result = repository.findById("1");

        // Then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().getStatus(), is(Status.DONE));
    }

    @Test
    @DisplayName("Should delete a search by ID")
    void shouldDeleteById() {
        // Given
        Search search = new Search("1", "keyword1", Status.ACTIVE);
        repository.save(search);

        // When
        repository.deleteById("1");
        Optional<Search> result = repository.findById("1");

        // Then
        assertThat(result.isPresent(), is(false));
    }

    @Test
    @DisplayName("Should find a search by keyword when it exists")
    void shouldFindByKeywordWhenExists() {
        // Given
        Search search = new Search("1", "keyword1", Status.ACTIVE);
        repository.save(search);

        // When
        Optional<Search> result = repository.findByKeyword("keyword1");

        // Then
        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(equalTo(search)));
    }

    @Test
    @DisplayName("Should return empty when searching by keyword that does not exist")
    void shouldReturnEmptyWhenKeywordDoesNotExist() {
        // Given
        Search search = new Search("1", "keyword1", Status.ACTIVE);
        repository.save(search);

        // When
        Optional<Search> result = repository.findByKeyword("nonexistent");

        // Then
        assertThat(result.isPresent(), is(false));
    }
}
