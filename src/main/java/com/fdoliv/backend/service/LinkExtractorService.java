package com.fdoliv.backend.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for extracting links from HTML content.
 * Resolves relative URLs and filters links based on a specified base URL.
 */
public class LinkExtractorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkExtractorService.class);

    /**
     * Regex to match anchor tags with href attributes, excluding mailto links.
     * This regex captures the URL in the href attribute.
     * It matches the pattern <a href="URL"> or <a href='URL'>, where URL can be
     * any valid URL except mailto links.
     */
    private static final String ANCHOR_HREF_REGEX = "<a\\b[^>]*?\\s+href\\s*=\\s*[\"'](?!mailto:)([^\"'>]*)[\"'][^>]*>";

    /**
     * Extracts links from the given HTML content.
     * Resolves relative URLs and filters links that start with the specified base URL.
     *
     * @param content the HTML content to extract links from
     * @param currentUrl the current URL being processed, used to resolve relative links
     * @param baseUrl the base URL to filter links
     * @return a list of extracted and resolved links
     */
    public List<String> extractLinks(String content, String currentUrl, String baseUrl) {
        LOGGER.debug("Extracting links from content for base URL: {}", baseUrl);
        // LOGGER.debug("Content: {}", content);
        List<String> links = new ArrayList<>();
        if (content == null || baseUrl == null) {
            return links;
        }

        var pattern = Pattern.compile(ANCHOR_HREF_REGEX, Pattern.CASE_INSENSITIVE);
        var matcher = pattern.matcher(content);

        while (matcher.find()) {
            String link = matcher.group(1);

            LOGGER.trace("Path founded: {}", link);
            if (!link.startsWith("http")) {
                link = resolveRelativeUrl(currentUrl, link);
            }

            if (link.startsWith(baseUrl)) {
                links.add(link);
            }
        }

        LOGGER.debug("New links founded: {}", links.size());
        return links;
    }

    /**
     * Resolves a relative URL against the current URL.
     *
     * @param currentUrl the current URL being processed
     * @param relativeUrl the relative URL to resolve
     * @return the resolved absolute URL, or the relative URL if resolution fails
     */
    private String resolveRelativeUrl(String currentUrl, String relativeUrl) {
        try {
            URL base = new URL(currentUrl);
            return new URL(base, relativeUrl).toString();
        } catch (Exception e) {
            LOGGER.error("Failed to resolve relative URL: {}", relativeUrl, e);
            return relativeUrl; 
        }
    }
}
