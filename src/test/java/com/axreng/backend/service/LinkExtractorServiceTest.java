package com.axreng.backend.service;

import org.junit.jupiter.api.Test;

import com.fdoliv.backend.service.LinkExtractorService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class LinkExtractorServiceTest {

    private final LinkExtractorService linkExtractorService = new LinkExtractorService();

    @Test
    void shouldExtractLinksFromHtml() {
        String html = "<a href=\"http://example.com\">Example</a>";
        String baseUrl = "http://example.com";

        List<String> links = linkExtractorService.extractLinks(html, baseUrl, baseUrl);

        assertThat("Should extract one link", links, hasSize(1));
        assertThat("Link should match the expected URL", links.get(0), is("http://example.com"));
    }

    @Test
    void shouldNotExtractInvalidLinks() {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<a href=\"mailto:example@example.com\">Email</a>");
        htmlBuilder.append("<a href=\"ftp://example.com/file\">FTP Link</a>");
        htmlBuilder.append("<a href=\"http://valid.com\">Valid Link</a>");
        String html = htmlBuilder.toString();
        String baseUrl = "http://valid.com";

        List<String> links = linkExtractorService.extractLinks(html, baseUrl, baseUrl);

        assertThat("Should extract only valid HTTP/HTTPS links", links, hasSize(1));
        assertThat("Link should match the valid URL", links.get(0), is("http://valid.com"));
    }
}
