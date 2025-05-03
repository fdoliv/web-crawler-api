package com.axreng.backend.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkExtractorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkExtractorService.class);
    /*
     * Regex to match anchor tags with href attributes, excluding mailto links.
     * This regex captures the URL in the href attribute.
     * It matches the pattern <a href="URL"> or <a href='URL'>, where URL can be
     * any valid URL except mailto links.
     */
    private static final String ANCHOR_HREF_REGEX = "<a\\b[^>]*?\\s+href\\s*=\\s*[\"'](?!mailto:)([^\"'>]*)[\"'][^>]*>";

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

            LOGGER.debug("paths founded: {}", link);
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
