package com.axreng.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private final Map<String, CrawlJob> activeJobs;
    private final AppConfig appConfig;
    private final KeywordSearchService keywordSearchService;
    private final LinkExtractorService linkExtractorService;
    private final HttpClientService httpClientService;

    
    public CrawlerService(SearchService repositoryService, 
            KeywordSearchService keywordSearchService, 
            LinkExtractorService linkExtractorService, 
            HttpClientService httpClientService, AppConfig appConfig) {
        this.repositoryService = repositoryService;
        this.appConfig = appConfig;
        this.executor = Executors.newFixedThreadPool(appConfig.getMaxThreads());
        this.keywordSearchService = keywordSearchService;
        this.linkExtractorService = linkExtractorService;
        this.httpClientService = httpClientService;
        this.activeJobs = new ConcurrentHashMap<>();

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
        int tasksPerJob = Math.max(1, appConfig.getMaxThreads() / totalJobs);
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
            String content = httpClientService.fetchContent(url);
            if (keywordSearchService.containsKeyword(content, job.getKeyword())) {
                job.addUrlToResults(url);
            }
            List<String> links = linkExtractorService.extractLinks(content, url, job.getBaseUrl());
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
        if (job != null && !job.hasMoreUrls() && job.isComplete()) {
            LOGGER.info("Crawl job for search ID {} is complete. Updating status to done.", searchId);
            repositoryService.updateSearchStatus(searchId);
            activeJobs.remove(searchId);
        }
    }
}