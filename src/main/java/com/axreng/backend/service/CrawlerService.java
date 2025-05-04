package com.axreng.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
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
    private final ThreadPoolExecutor executor;
    private final Map<String, CrawlJob> activeJobs;
    private final AppConfig appConfig;
    private final KeywordSearchService keywordSearchService;
    private final LinkExtractorService linkExtractorService;
    private final HttpClientService httpClientService;
    private final ThreadMonitorService threadMonitorService;
    private final ReentrantLock schedulingLock = new ReentrantLock();

    
    public CrawlerService(SearchService repositoryService, 
            KeywordSearchService keywordSearchService, 
            LinkExtractorService linkExtractorService, 
            HttpClientService httpClientService, AppConfig appConfig) {
        this.repositoryService = repositoryService;
        this.appConfig = appConfig;
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(appConfig.getMaxThreads());
        this.keywordSearchService = keywordSearchService;
        this.linkExtractorService = linkExtractorService;
        this.httpClientService = httpClientService;
        this.activeJobs = new ConcurrentHashMap<>();
        this.threadMonitorService = new ThreadMonitorService(executor, activeJobs);

    }
    
    public String startCrawl(String searchId) throws SearchNotFoundException {
        
        LOGGER.info("Starting crawl for search ID: {}", searchId);
        Search search = repositoryService.findSearchById(searchId);

        LOGGER.info("Creating a job for search ID: {}", searchId);
        CrawlJob job = new CrawlJob(searchId, search.getKeyword(), appConfig.getBaseUrl(), repositoryService);
        activeJobs.put(searchId, job);
        
        executor.submit(new JobWorker(job));
        LOGGER.info("Crawl job for search ID {} started.", searchId);
        threadMonitorService.monitorThreads();
        
        return searchId;
    }
        
    private void processUrl(CrawlJob job, String url) {

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
        finally{
            job.getLock().lock();
            job.markUrlAsProcessed(url);
            job.getLock().unlock();
        }
 
    }
    
    private void finishJob(String searchId) {
        schedulingLock.lock();
        try {
            CrawlJob job = activeJobs.remove(searchId);
            if (job != null) {
                try {
                    repositoryService.updateSearchStatus(searchId);
                    // metrics.activeJobs.dec();
                    LOGGER.info("Completed job for search ID: {}", searchId);
                } catch (SearchNotFoundException e) {
                    LOGGER.error("Search not found during cleanup: {}", searchId, e);
                }
            }
        } finally {
            schedulingLock.unlock();
        }
    }

    public synchronized void shutdown() {
        if (!executor.isShutdown()) {
            LOGGER.info("Initiating graceful shutdown...");
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    private class JobWorker implements Runnable {
        private final CrawlJob job;
        private final ReentrantLock jobLock = new ReentrantLock();

        public JobWorker(CrawlJob job) {
            this.job = job;
        }

        @Override
        public void run() {
            try {
                while (!job.isComplete() && !Thread.currentThread().isInterrupted()) {
                    jobLock.lock();
                    try {
                        String url = job.getNextUrl();

                        LOGGER.debug("Crawler Executor Status - Active threads: {}, Queue size: {}, Active jobs: {}", 
                            executor.getActiveCount(), 
                            executor.getQueue().size(), 
                            activeJobs.size());
                        for (CrawlJob job : activeJobs.values()) {
                            LOGGER.debug("Job ID: {}, Pending URLs: {}, Processed URLs: {}, Complete: {}", 
                                    job.getSearchId(), 
                                    job.getPendingUrls().size(), 
                                    job.getProcessedUrlsCount(), 
                                    job.isComplete());
                        }

                        if (url != null) {
                            processUrl(job, url);
                        } else {
                            LOGGER.debug("No more URLs to process for job ID: {}", job.getSearchId());
                            break; 
                        }
                    } finally {
                        jobLock.unlock();
                        Thread.yield();
                    }
                }
                LOGGER.info("Job ID {} is complete or interrupted.", job.getSearchId());
            } finally {
                finishJob(job.getSearchId());
            }
        }
    }
}