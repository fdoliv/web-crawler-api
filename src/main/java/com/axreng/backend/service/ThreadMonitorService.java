package com.axreng.backend.service;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axreng.backend.crawler.CrawlJob;

/**
 * Service for monitoring the status of threads in the crawler's thread pool.
 * Logs information about active threads, queue size, and active crawl jobs at regular intervals.
 */
public class ThreadMonitorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadMonitorService.class);

    /**
     * The thread pool executor to monitor.
     */
    private final ThreadPoolExecutor executor;

    /**
     * The map of active crawl jobs being monitored.
     */
    private final Map<String, CrawlJob> activeJobs;

    /**
     * The interval (in seconds) at which monitoring logs are generated.
     */
    private final int MONITORING_INTERVAL = 1; // in seconds

    /**
     * Constructs a ThreadMonitorService with the specified thread pool executor and active jobs map.
     *
     * @param executor the thread pool executor to monitor
     * @param activeJobs the map of active crawl jobs
     */
    public ThreadMonitorService(ThreadPoolExecutor executor, Map<String, CrawlJob> activeJobs) {
        this.executor = executor;
        this.activeJobs = activeJobs;
    }

    /**
     * Starts monitoring the thread pool and active jobs.
     * Logs the status of the thread pool and details of active jobs at regular intervals.
     */
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
