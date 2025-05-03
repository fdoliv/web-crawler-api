package com.axreng.backend.util;

import java.util.UUID;

import com.axreng.backend.repository.SearchRepository;


public class IDGenerator {

    private static final int ID_LENGTH = 8;


    public static String generateUniqueId(SearchRepository searchRepository) {
        var id = IDGenerator.generateAlphanumericID();
        var search = searchRepository.findById(id);
        while (search.isPresent()) {
            id = IDGenerator.generateAlphanumericID();
            search = searchRepository.findById(id);
        }
        return id;
    }

    public static String generateAlphanumericID() {
        return UUID.randomUUID()
                   .toString()
                   .replaceAll("-", "")
                   .substring(0, ID_LENGTH);
    }
}
