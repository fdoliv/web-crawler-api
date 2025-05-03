package com.axreng.backend.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.crawler.CrawlJob;
import com.axreng.backend.exception.SearchNotFoundException;
import com.axreng.backend.model.Search;
import com.axreng.backend.util.AppConfig;

public class CrawlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);
    private final SearchService repositoryService;
    private final ExecutorService executor;
    private final int maxThreads;
    private final Map<String, CrawlJob> activeJobs = new ConcurrentHashMap<>();
    private final AppConfig appConfig = AppConfig.getInstance();
    
    public CrawlerService(SearchService repositoryService) {
        this.repositoryService = repositoryService;
        this.maxThreads = Runtime.getRuntime().availableProcessors() + 1;
        this.executor = Executors.newFixedThreadPool(maxThreads);
    }
    
    public String startCrawl(String searchId) throws SearchNotFoundException {
        LOGGER.info("Starting crawl for search ID: {}", searchId);
        Search search = repositoryService.findSearchById(searchId);

        LOGGER.info("Creating a job for search ID: {}", searchId);
        CrawlJob job = new CrawlJob(searchId, search.getKeyword(), appConfig.getBaseUrl(), repositoryService);
        activeJobs.put(searchId, job);
        
        scheduleNextBatch();
        
        return searchId;
    }
    
    private void scheduleNextBatch() {
        LOGGER.info("Distribuing tasks to active jobs");
        List<CrawlJob> jobs = new ArrayList<>(activeJobs.values());
        int totalJobs = jobs.size();
        LOGGER.debug( "Total active jobs: {}", totalJobs);     
        if (totalJobs == 0) return;
        
        // Calcular quantas tarefas cada job deve receber
        int tasksPerJob = Math.max(1, maxThreads / totalJobs);
        // tasksPerJob = 2;
        LOGGER.debug("Total tasks per Job: {}", tasksPerJob);

        for (CrawlJob job : jobs) {
            LOGGER.debug("Job: {}", job);
            for (int i = 0; i < tasksPerJob && job.hasMoreUrls(); i++) {
                String nextUrl = job.getNextUrl();
                LOGGER.debug("Next url {}", nextUrl);
                executor.submit(() -> {
                    processUrl(job, nextUrl);
                    
                });
            }
        }
    }
    
    private synchronized void processUrl(CrawlJob job, String url) {

        LOGGER.debug("Processing URL: {}", url);
        try {
            // Processar a URL, verificar o termo, extrair links
            String content = fetchContent(url);
            boolean containsKeyword = checkForKeyword(content, job.getKeyword());
            
            if (containsKeyword) {
                repositoryService.addUrlToSearch(job.getSearchId(), url);
            }
            
            // Extrair e adicionar novos links à fila do job
            List<String> links = extractLinks(content, url, job.getBaseUrl());
            job.addNewUrls(links);
            
        } catch (Exception e) {
            LOGGER.error("Error processing URL: {}", url, e);
        }
        synchronized (job){
            if (job.hasMoreUrls()) {
                scheduleNextBatch();
            } else if (job.isComplete()) {
                finishJob(job.getSearchId());
            }
        }

    }
    
    private void finishJob(String searchId) {
        CrawlJob job = activeJobs.get(searchId);
        if (job != null && !job.hasMoreUrls() && !job.hasActiveTasks()) {
            repositoryService.updateSearchStatus(searchId);
            activeJobs.remove(searchId);
            LOGGER.info("Crawl job for search ID {} completed.", searchId);
        }
    }
    
    private String fetchContent(String url) throws Exception {
        LOGGER.debug("Fetching content from URL: {}", url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // Timeout de conexão
        connection.setReadTimeout(5000);    // Timeout de leitura

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed to fetch content. HTTP response code: " + responseCode);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }

    private boolean checkForKeyword(String content, String keyword) {
        LOGGER.debug("Checking for keyword '{}' in content.", keyword);
        if (content == null || keyword == null) {
            return false;
        }
        return content.toLowerCase().contains(keyword.toLowerCase());
    }

    private List<String> extractLinks(String content, String currentUrl, String baseUrl) {
        LOGGER.debug("Extracting links from content for base URL: {}", baseUrl);
        // LOGGER.debug("Content: {}", content);
        List<String> links = new ArrayList<>();
        if (content == null || baseUrl == null) {
            return links;
        }

        var regex = "<a\\b[^>]*?\\s+href\\s*=\\s*[\"'](?!mailto:)([^\"'>]*)[\"'][^>]*>";
        var pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        var matcher = pattern.matcher(content);

        while (matcher.find()) {
            String link = matcher.group(1);

            LOGGER.debug("paths founded: {}", link);
            // Resolver links relativos
            if (!link.startsWith("http")) {
                link = resolveRelativeUrl(currentUrl, link);
            }

            // Adicionar apenas links que começam com a URL base
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
            return relativeUrl; // Retorna o link original se não puder resolver
        }
    }
}