package com.axreng.backend.service;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.crawler.CrawlJob;

public class ThreadMonitorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadMonitorService.class);
    private final ThreadPoolExecutor executor;
    private final Map<String, CrawlJob> activeJobs;
    private final int MONITORING_INTERVAL = 10; // in seconds

    public ThreadMonitorService(ThreadPoolExecutor executor, Map<String, CrawlJob> activeJobs) {
        this.executor = executor;
        this.activeJobs = activeJobs;
    }

    public void monitorThreads() {
        ScheduledExecutorService monitor = Executors.newScheduledThreadPool(1);
        monitor.scheduleAtFixedRate(() -> {
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
        }, 1, MONITORING_INTERVAL, TimeUnit.SECONDS);
    }
}
