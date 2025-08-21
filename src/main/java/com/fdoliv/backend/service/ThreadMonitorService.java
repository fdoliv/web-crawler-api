package com.fdoliv.backend.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;

import com.fdoliv.backend.util.ApplicationConfiguration;
import com.sun.management.OperatingSystemMXBean; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * The interval (in seconds) at which monitoring logs are generated.
     */
    private final int MONITORING_INTERVAL = 30; // in seconds
    
    /**
     * The maximum CPU usage threshold for increasing the thread pool size.
     */
    private final int MAX_CPU_THRESHOLD = 70; // in percentage
    
    /**
     * The maximum CPU limit for decreasing the thread pool size.
     */
    private final int MAX_CPU_LIMIT = 90; // in percentage

    /**
     * The threshold for the number of jobs in the queue to trigger thread pool size adjustment.
     */
    private final int MAX_QUANTITY_JOBS_THRESHOLD = 30; 

    /**
     * The minimum number of jobs in the queue to trigger thread pool size adjustment.
     */
    private final int MIN_QUANTITY_JOBS_THRESHOLD = 10; 

    /**
     * The ScheduledExecutorService for monitoring.
     */
    private ScheduledExecutorService monitor;

    /**
     * The application configuration instance.
     */
    private final ApplicationConfiguration config;

    /**
     * Constructs a ThreadMonitorService with the specified thread pool executor, active jobs map, and application configuration.
     *
     * @param executor the thread pool executor to monitor
     * @param activeJobs the map of active crawl jobs
     * @param config the application configuration
     */
    public ThreadMonitorService(ThreadPoolExecutor executor, ApplicationConfiguration config) {
        this.executor = executor;
        this.config = config;
    }

    /**
     * Starts monitoring the thread pool and active jobs.
     * Logs the status of the thread pool and details of active jobs at regular intervals.
     */
    public void monitorThreads() {
        monitor = Executors.newScheduledThreadPool(1);
        monitor.scheduleAtFixedRate(() -> {
            int pendingJobs = executor.getQueue().size();
            int currentPoolSize = executor.getCorePoolSize();
            int activeThreads = executor.getActiveCount();

            LOGGER.debug("Crawler Executor Status - Active threads: {}, Queue size: {}",
                    activeThreads, 
                    pendingJobs);

            // Dynamically adjust thread pool size based on system load and job count
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double cpuUsage = osBean.getCpuLoad() * 100; 

            if (cpuUsage < MAX_CPU_THRESHOLD && pendingJobs > MAX_QUANTITY_JOBS_THRESHOLD && currentPoolSize < config.getMaxThreads()) {
                int newPoolSize = executor.getCorePoolSize() + 2;
                executor.setCorePoolSize(newPoolSize);
                LOGGER.info("Increased thread pool size to {} based on system load and job count", newPoolSize);
            } else if ((cpuUsage > MAX_CPU_LIMIT || pendingJobs < MIN_QUANTITY_JOBS_THRESHOLD) && currentPoolSize > config.getMinThreads()) {
                int newPoolSize = Math.max(1, executor.getCorePoolSize() - 1);
                executor.setCorePoolSize(newPoolSize);
                LOGGER.info("Decreased thread pool size to {} based on system load and job count", newPoolSize);
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
