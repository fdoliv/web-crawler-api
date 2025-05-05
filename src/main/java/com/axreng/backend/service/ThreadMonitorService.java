package com.axreng.backend.service;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

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
    private final int MONITORING_INTERVAL = 30; // in seconds

    /**
     * The ScheduledExecutorService for monitoring.
     */
    private ScheduledExecutorService monitor;

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
        monitor = Executors.newScheduledThreadPool(1);
        monitor.scheduleAtFixedRate(() -> {
            LOGGER.debug("Crawler Executor Status - Active threads: {}, Queue size: {}, Active jobs: {}", 
                    executor.getActiveCount(), 
                    executor.getQueue().size(), 
                    activeJobs.size());

            // Log detailed job metrics
            activeJobs.values().stream()
                .filter(job -> !job.isComplete()) // Only log jobs that are still processing
                .forEach(job -> LOGGER.debug("Job ID: {}, Pending URLs: {}, Processing URLs: {}, Processed URLs: {}, Complete: {}", 
                        job.getSearchId(), 
                        job.getPendingUrlsCount(),
                        job.getProcessingUrlsCount(), 
                        job.getProcessedUrlsCount(), 
                        job.isComplete()));

            // Dynamically adjust thread pool size based on system load
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            double systemLoad = osBean.getSystemLoadAverage();
            int optimalPoolSize = Math.max(1, (int) (Runtime.getRuntime().availableProcessors() * 0.9));
            if (systemLoad < 0.7 && executor.getCorePoolSize() < optimalPoolSize) {
                executor.setCorePoolSize(optimalPoolSize);
                LOGGER.info("Increased thread pool size to {}", optimalPoolSize);
            } else if (systemLoad > 0.9 && executor.getCorePoolSize() > 1) {
                executor.setCorePoolSize(executor.getCorePoolSize() - 1);
                LOGGER.info("Decreased thread pool size to {}", executor.getCorePoolSize());
            }
        }, 1, MONITORING_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * Shuts down the ThreadMonitorService.
     * Ensures that the ScheduledExecutorService is properly terminated.
     */
    public void shutdown() {
        LOGGER.info("Shutting down ThreadMonitorService...");
        if (monitor != null) {
            monitor.shutdown();
        }
    }
}
