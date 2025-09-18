package br.dev.dias.crawler;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import br.dev.dias.exception.SearchNotFoundException;
import br.dev.dias.service.SearchService;
import br.dev.dias.util.StringUtils;

/**
 * Represents a job for crawling URLs starting from a base URL.
 * Manages pending and visited URLs, and interacts with a repository service to store results.
 */
public class CrawlJob {
    private final String searchId;
    private final String keyword;
    private final String baseUrl;
    private final SearchService repoService;
    private final Queue<String> pendingUrls;
    private final Set<String> visitedUrls;
    private final AtomicInteger pendingUrlsCounter;
    private final AtomicInteger visitedUrlsCounter;
    private final AtomicInteger processingUrlsCounter;
    private final Lock lock;

    /**
     * Constructs a CrawlJob with the specified parameters.
     *
     * @param searchId the unique identifier for the search
     * @param keyword the keyword to search for
     * @param baseUrl the starting URL for the crawl
     * @param repoService the service for storing search results
     */
    public CrawlJob(String searchId, String keyword, String baseUrl, SearchService repoService) {
        this.searchId = searchId;
        this.keyword = keyword;
        this.baseUrl = baseUrl;
        this.repoService = repoService;
        this.pendingUrls = new ConcurrentLinkedQueue<>();
        this.visitedUrls = ConcurrentHashMap.newKeySet();
        this.pendingUrls.add(baseUrl);
        this.pendingUrlsCounter = new AtomicInteger(1);
        this.visitedUrlsCounter = new AtomicInteger(0);
        processingUrlsCounter = new AtomicInteger(0);
        this.lock = new ReentrantLock();

    }

    /**
     * Retrieves the next URL to be processed from the queue of pending URLs.
     *
     * @return the next URL, or null if no URLs are pending
     */
    public String getNextUrl() {
        pendingUrlsCounter.decrementAndGet();
        processingUrlsCounter.incrementAndGet();
        return pendingUrls.poll();
    }

    /**
     * Adds new URLs to the queue of pending URLs, avoiding duplicates and already visited URLs.
     *
     * @param urls the list of URLs to add
     */
    public void addNewUrls(List<String> urls) {
        urls.removeAll(visitedUrls);
        for (String url : urls) {
            if (!pendingUrls.contains(url)) {
                pendingUrls.add(url);
                pendingUrlsCounter.incrementAndGet();
            }
        }
    }

    /**
     * Checks if there are more URLs to process.
     *
     * @return true if there are pending URLs, false otherwise
     */
    public boolean hasMoreUrls() {
        return pendingUrlsCounter.get() > 0;
    }

    /**
     * Checks if the crawl job is complete (no pending or processing URLs).
     *
     * @return true if the job is complete, false otherwise
     */
    public boolean isComplete() {
        return pendingUrlsCounter.get() == 0 && processingUrlsCounter.get() == 0;
    }

    /**
     * Gets the unique identifier for the search.
     *
     * @return the search ID
     */
    public String getSearchId() {
        return searchId;
    }

    /**
     * Gets the keyword being searched for.
     *
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Gets the base URL for the crawl.
     *
     * @return the base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Gets the repository service used for storing search results.
     *
     * @return the repository service
     */
    public SearchService getRepoService() {
        return repoService;
    }

    /**
     * Gets the set of visited URLs.
     *
     * @return the set of visited URLs
     */
    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }

    /**
     * Gets the queue of pending URLs.
     *
     * @return the queue of pending URLs
     */
    public Queue<String> getPendingUrls() {
        return pendingUrls;
    }

    /**
     * Gets the lock used for synchronizing access to shared resources.
     *
     * @return the lock
     */
    public Lock getLock() {
        return lock;
    }

    /**
     * Adds a URL to the search results in the repository service.
     *
     * @param url the URL to add
     * @throws SearchNotFoundException if the search ID is not found in the repository
     */
    public void addUrlToResults(String url) throws SearchNotFoundException {
        repoService.addUrlToSearch(getSearchId(), url);
    }

    /**
     * Gets the count of pending URLs.
     *
     * @return the count of pending URLs
     */
    public int getPendingUrlsCount() {
        return pendingUrlsCounter.get();
    }

    /**
     * Gets the count of processed (visited) URLs.
     *
     * @return the count of processed URLs
     */
    public int getProcessedUrlsCount() {
        return visitedUrlsCounter.get();
    }

    public int getProcessingUrlsCount() {
        return processingUrlsCounter.get();
    }

    /**
     * Marks a URL as processed by adding it to the visited set and incrementing the counter.
     *
     * @param url the URL to mark as processed
     */
    public void markUrlAsProcessed(String url) {
        visitedUrls.add(url);
        visitedUrlsCounter.incrementAndGet();
        processingUrlsCounter.decrementAndGet();
    }

    /**
     * Returns a string representation of the crawl job, including search ID, keyword, base URL, and pending URL count.
     *
     * @return a JSON-like string representation of the crawl job
     */
    @Override
    public String toString() {
        return String.format(
            "{\"searchId\":\"%s\", \"keyword\":\"%s\", \"baseUrl\":\"%s\", \"pendingUrlsCount\":%d}",
            searchId, StringUtils.escapeJson(keyword), StringUtils.escapeJson(baseUrl), pendingUrls.size()
        );
    }
}
