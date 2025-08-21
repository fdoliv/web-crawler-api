package com.fdoliv.backend.util;

import java.util.UUID;

import com.fdoliv.backend.repository.SearchRepository;

/**
 * Utility class for generating unique alphanumeric IDs.
 * Ensures that generated IDs are unique within the provided repository.
 */
public class IDGenerator {

    /**
     * The length of the generated alphanumeric ID.
     */
    private static final int ID_LENGTH = 8;

    /**
     * Generates a unique alphanumeric ID that does not already exist in the given repository.
     *
     * @param searchRepository the repository to check for existing IDs
     * @return a unique alphanumeric ID
     */
    public static String generateUniqueId(SearchRepository searchRepository) {
        var id = IDGenerator.generateAlphanumericID();
        var search = searchRepository.findById(id);
        while (search.isPresent()) {
            id = IDGenerator.generateAlphanumericID();
            search = searchRepository.findById(id);
        }
        return id;
    }

    /**
     * Generates a random alphanumeric ID of a fixed length.
     *
     * @return a random alphanumeric ID
     */
    public static String generateAlphanumericID() {
        return UUID.randomUUID()
                   .toString()
                   .replaceAll("-", "")
                   .substring(0, ID_LENGTH);
    }
}
