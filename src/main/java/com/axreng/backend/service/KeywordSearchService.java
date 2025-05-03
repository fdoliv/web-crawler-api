package com.axreng.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeywordSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeywordSearchService.class);


    public boolean containsKeyword(String content, String keyword) {
        LOGGER.debug("Checking for keyword '{}' in content.", keyword);
        if (content == null || keyword == null) {
            return false;
        }
        return content.toLowerCase().contains(keyword.toLowerCase());
    }

}
