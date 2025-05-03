package com.axreng.backend.crawler;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.axreng.backend.exception.SearchNotFoundException;
import com.axreng.backend.service.SearchService;
import com.axreng.backend.util.StringUtils;

public class CrawlJob {
    private final String searchId;
    private final String keyword;
    private final String baseUrl;
    private final SearchService repoService;
    private final Queue<String> pendingUrls = new ConcurrentLinkedQueue<>();
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean isComplete = new AtomicBoolean(false);
    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final AtomicInteger processedUrls = new AtomicInteger(0);
    // private final AtomicInteger scheduledUrls = new AtomicInteger(0);

    public CrawlJob(String searchId, String keyword, String baseUrl, SearchService repoService) {
        this.searchId = searchId;
        this.keyword = keyword;
        this.baseUrl = baseUrl;
        this.pendingUrls.add(baseUrl);
        this.visitedUrls.add(baseUrl);
        this.repoService = repoService;
    }
    
    public String getNextUrl() {
        // scheduledUrls.decrementAndGet();
        return pendingUrls.poll();
    }
    
    public void addNewUrls(List<String> urls) {
        for (String url : urls) {
            // add url only if not visited
            if (!visitedUrls.contains(url)) {
                visitedUrls.add(url);
                pendingUrls.add(url);
            }
        }
        // scheduledUrls.addAndGet(urls.size());
    }
    
    public boolean hasMoreUrls() {
        return !pendingUrls.isEmpty();
    }
    
    public boolean isComplete() {
        return getPendingUrlsCount() == 0;
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

    // public boolean hasActiveTasks() {
    //     return activeTasks.get() > 0;
    // }

    // public void incrementActiveTasks() {
    //     activeTasks.incrementAndGet();
    // }

    // public void decrementActiveTasks() {
    //     activeTasks.decrementAndGet();
    //     if (activeTasks.get() == 0 && isComplete()) {
    //         isComplete.set(true);
    //     }
    // }

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
        return pendingUrls.size();
    }

    public int getProcessedUrlsCount() {
        return visitedUrls.size();
    }
}
