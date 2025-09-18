package br.dev.dias.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for searching keywords within a given content.
 */
public class KeywordSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeywordSearchService.class);

    /**
     * Checks if the specified keyword is present in the given content.
     *
     * @param content the content to search within
     * @param keyword the keyword to search for
     * @return true if the keyword is found in the content, false otherwise
     */
    public boolean containsKeyword(String content, String keyword) {
        LOGGER.debug("Checking for keyword '{}' in content.", keyword);
        if (content == null || keyword == null) {
            return false;
        }
        return content.toLowerCase().contains(keyword.toLowerCase());
    }

}
