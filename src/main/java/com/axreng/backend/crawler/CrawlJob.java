package com.axreng.backend.crawler;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.axreng.backend.exception.SearchNotFoundException;
import com.axreng.backend.service.SearchService;
import com.axreng.backend.util.StringUtils;

public class CrawlJob {
    private final String searchId;
    private final String keyword;
    private final String baseUrl;
    private final SearchService repoService;
    private final Queue<String> pendingUrls;
    private final Set<String> visitedUrls ;
    private final AtomicInteger pendingUrlsCounter;
    private final AtomicInteger visitedUrlsCounter;
    private final Lock lock;
    
    public CrawlJob(String searchId, String keyword, String baseUrl, SearchService repoService) {
        this.searchId = searchId;
        this.keyword = keyword;
        this.baseUrl = baseUrl;
        this.repoService = repoService;
        this.pendingUrls = new ConcurrentLinkedQueue<>();
        this.visitedUrls = ConcurrentHashMap.newKeySet();
        this.pendingUrls.add(baseUrl);
        this.pendingUrlsCounter = new AtomicInteger(1);
        visitedUrlsCounter = new AtomicInteger(0);
        this.lock = new ReentrantLock();
    }
    
    public String getNextUrl() {
        pendingUrlsCounter.decrementAndGet();
        return pendingUrls.poll();
    }
    
    public void addNewUrls(List<String> urls) {
        urls.removeAll(visitedUrls);
        for (String url : urls) {
            if (!pendingUrls.contains(url)) {
                pendingUrls.add(url);
                pendingUrlsCounter.incrementAndGet();
            }
        }
    }
    
    public boolean hasMoreUrls() {
        return pendingUrlsCounter.get() > 0;
    }
    
    public boolean isComplete() {
        return pendingUrlsCounter.get() == 0 && pendingUrls.size() == 0;
    }
    
    public String getSearchId() {
        return searchId;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public SearchService getRepoService() {
        return repoService;
    }

    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }

    public Queue<String> getPendingUrls() {
        return pendingUrls;
    }

    public Lock getLock() {
        return lock;
    }

    @Override
    public String toString() {
        return String.format(
            "{\"searchId\":\"%s\", \"keyword\":\"%s\", \"baseUrl\":\"%s\", \"pendingUrlsCount\":%d}",
            searchId, StringUtils.escapeJson(keyword), StringUtils.escapeJson(baseUrl), pendingUrls.size()
        );
    }

    public void addUrlToResults(String url) throws SearchNotFoundException {
        repoService.addUrlToSearch(getSearchId(), url);
    }

    public int getPendingUrlsCount() {
        return pendingUrlsCounter.get();
    }

    public int getProcessedUrlsCount() {
        return visitedUrlsCounter.get();
    }

    public void markUrlAsProcessed(String url) {
        visitedUrls.add(url);
        visitedUrlsCounter.incrementAndGet();
    }
}
