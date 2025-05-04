package com.axreng.backend.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlCacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlCacheService.class);
    private static final long CACHE_EXPIRATION_MINUTES = 5;
    private static final int MAX_CACHE_SIZE = 2000; 
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public HtmlCacheService() {
        LOGGER.info("Initializing HtmlCacheService with expiration time of {} minutes.", CACHE_EXPIRATION_MINUTES);
        scheduler.scheduleAtFixedRate(this::removeExpiredEntries, 1, 1, TimeUnit.MINUTES);
    }

    public String get(String url) {
        LOGGER.debug("Fetching content for URL: {}", url);
        CacheEntry entry = cache.get(url);
        if (entry != null && !entry.isExpired()) {
            LOGGER.debug("Cache hit for URL: {}", url);
            entry.updateLastAccessTime();
            return entry.getContent();
        }
        LOGGER.debug("Cache miss for URL: {}", url);
        return null;
    }

    public void put(String url, String content) {
        if (cache.size() >= MAX_CACHE_SIZE) {
            LOGGER.warn("Cache size exceeded the maximum limit. Consider increasing the limit or optimizing usage.");
            return;
        }
        LOGGER.debug("Adding content to cache for URL: {}", url);
        cache.put(url, new CacheEntry(content));
    }

    public void shutdown() {
        LOGGER.info("Shutting down HtmlCacheService...");
        scheduler.shutdown();
    }

    private void removeExpiredEntries() {
        LOGGER.debug("Running cache cleanup for expired entries.");
        long now = System.currentTimeMillis();
        int initialSize = cache.size();
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
        int finalSize = cache.size();
        LOGGER.info("Cache cleanup completed. Removed {} expired entries.", initialSize - finalSize);
    }

    private static class CacheEntry {
        private final String content;
        private volatile long lastAccessTime;

        public CacheEntry(String content) {
            this.content = content;
            this.lastAccessTime = System.currentTimeMillis();
            LOGGER.debug("Created new cache entry.");
        }

        public String getContent() {
            return content;
        }

        public void updateLastAccessTime() {
            this.lastAccessTime = System.currentTimeMillis();
            LOGGER.debug("Updated last access time for cache entry.");
        }

        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }

        public boolean isExpired(long currentTime) {
            boolean expired = (currentTime - lastAccessTime) > TimeUnit.MINUTES.toMillis(CACHE_EXPIRATION_MINUTES);
            if (expired) {
                LOGGER.debug("Cache entry expired.");
            }
            return expired;
        }
    }
}
