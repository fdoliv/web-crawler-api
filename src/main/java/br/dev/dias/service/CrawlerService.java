package br.dev.dias.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.dev.dias.crawler.CrawlJob;
import br.dev.dias.exception.FailedFetchContentException;
import br.dev.dias.exception.HttpRequestFailedException;
import br.dev.dias.exception.SearchNotFoundException;
import br.dev.dias.model.Search;
import br.dev.dias.util.ApplicationConfiguration;

/**
 * Service responsible for managing and executing crawl jobs.
 * Handles job scheduling, URL processing, and thread management.
 */
public class CrawlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);
    private final SearchService repositoryService;
    private final ThreadPoolExecutor executor;
    private final Map<String, CrawlJob> activeJobs;
    private final ApplicationConfiguration appConfig;
    private final KeywordSearchService keywordSearchService;
    private final LinkExtractorService linkExtractorService;
    private final HttpClientService httpClientService;
    private final ThreadMonitorService threadMonitorService;
    private final ReentrantLock schedulingLock;
    private final HtmlCacheService htmlCacheService;

    /**
     * Constructs a CrawlerService with the specified dependencies.
     *
     * @param repositoryService the service for managing search data
     * @param keywordSearchService the service for searching keywords in content
     * @param linkExtractorService the service for extracting links from content
     * @param httpClientService the service for making HTTP requests
     * @param appConfig the application configuration
     */
    public CrawlerService(SearchService repositoryService, 
            KeywordSearchService keywordSearchService, 
            LinkExtractorService linkExtractorService, 
            HttpClientService httpClientService, ApplicationConfiguration appConfig) {
        this.repositoryService = repositoryService;
        this.appConfig = appConfig;
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(appConfig.getMinThreads());
        this.keywordSearchService = keywordSearchService;
        this.linkExtractorService = linkExtractorService;
        this.httpClientService = httpClientService;
        this.activeJobs = new ConcurrentHashMap<>();
        this.threadMonitorService = new ThreadMonitorService(executor, appConfig);
        this.schedulingLock = new ReentrantLock();
        this.htmlCacheService = new HtmlCacheService();
    }

    /**
     * Starts a new crawl job for the specified search ID.
     *
     * @param searchId the ID of the search to start crawling for
     * @return the search ID
     * @throws SearchNotFoundException if the search ID is not found
     */
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

    /**
     * Processes a single URL for the given crawl job.
     *
     * @param job the crawl job
     * @param url the URL to process
     */
    private void processUrl(CrawlJob job, String url) {
        LOGGER.debug("Processing URL: {}", url);
        int retryCount = 0;
        final int maxRetries = 3;
        final long retryInterval = 30000; // 30 seconds in milliseconds

        while (retryCount < maxRetries) {
            if (Thread.currentThread().isInterrupted()) {
                LOGGER.warn("Thread interrupted while processing URL: {}. Exiting process.", url);
                return;
            }
            try {
                String content = htmlCacheService.get(url);
                if (content == null) {
                    content = httpClientService.fetchContent(url);
                    htmlCacheService.put(url, content);
                }
                if (keywordSearchService.containsKeyword(content, job.getKeyword())) {
                    job.addUrlToResults(url);
                }
                List<String> links = linkExtractorService.extractLinks(content, url, job.getBaseUrl());
                job.addNewUrls(links);
                job.markUrlAsProcessed(url);
                return; 
            } catch(HttpRequestFailedException hrfe) {
                LOGGER.error("HTTP request failed for URL: {}. Error: {}", url, hrfe.getMessage());
                job.markUrlAsProcessed(url);
                return; 

            } catch (FailedFetchContentException e) {
                retryCount++;
                LOGGER.warn("Job of search {} has a connection issue for URL: {}. Retrying {}/{} in {} seconds...", 
                    job.getSearchId(), url, retryCount, maxRetries, retryInterval / 1000);
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Retry interrupted for URL: {}", url, ie);
                    return;
                }
            } catch (SearchNotFoundException snfe) {
                LOGGER.error("Search {} not found for URL: {}.", job.getSearchId(), url, snfe);
                break;
            }
        }

        if (retryCount >= maxRetries) {
            LOGGER.error("Max retries reached for URL: {}. Marking task as done.", url);
            job.getLock().lock();
            try {
                job.markUrlAsProcessed(url);
                if (!job.hasMoreUrls()) {
                    finishJob(job.getSearchId());
                }
            } finally {
                job.getLock().unlock();
            }
        }
    }

    /**
     * Finishes a crawl job by updating its status and removing it from active jobs.
     *
     * @param searchId the ID of the search to finish
     */
    private void finishJob(String searchId) {
        schedulingLock.lock();
        try {
            CrawlJob job = activeJobs.remove(searchId);
            if (job != null) {
                LOGGER.info("Removed job with ID {} from activeJobs. Remaining jobs: {}", searchId, activeJobs.size());
                try {
                    repositoryService.updateSearchStatus(searchId);
                } catch (SearchNotFoundException e) {
                    LOGGER.error("Search not found during cleanup: {}", searchId, e);
                }
            }
        } finally {
            schedulingLock.unlock();
        }
    }

    /**
     * Shuts down the CrawlerService, ensuring all threads are properly terminated.
     */
    public void shutdown() {
        LOGGER.info("Shutting down CrawlerService...");
        executor.shutdown();
        threadMonitorService.shutdown();
        htmlCacheService.shutdown();
    }

    /**
     * Worker class responsible for executing a crawl job.
     */
    private class JobWorker implements Runnable {
        private final CrawlJob job;

        /**
         * Constructs a JobWorker for the specified crawl job.
         *
         * @param job the crawl job to execute
         */
        public JobWorker(CrawlJob job) {
            this.job = job;
        }

        @Override
        public void run() {
            try {
                while (!job.isComplete() && !Thread.currentThread().isInterrupted()) {
                    String url = job.getNextUrl();
                    if (url != null) {
                        processUrl(job, url);
                    } else {
                        LOGGER.debug("No more URLs to process for job ID: {}", job.getSearchId());
                        break;
                    }
                    Thread.yield(); 
                }
                LOGGER.info("Job ID {} is complete or interrupted.", job.getSearchId());
            } finally {
                finishJob(job.getSearchId());
            }
        }
    }
}