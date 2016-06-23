/* ********************************************************************************
 * MonitoringTaskRequestHandler.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.monitoring;

import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>MonitoringTaskRequestHandler</code><br>
 *
 * @author VMware, Inc.
 */
public class MonitoringTaskRequestHandler
{
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( MonitoringTaskRequestHandler.class );

    /** The monitoring thread pool size. */
    private static int MONITORING_THREAD_POOL_SIZE = 40;

    /** The instance. */
    private static volatile MonitoringTaskRequestHandler instance = new MonitoringTaskRequestHandler();

    /** The server_monitor_executor. */
    private ExecutorService executorService = null;

    /** The monitor server comp service. */
    private CompletionService<MonitoringTaskResponse> completionService = null;

    /** The monitoring server tasks. */
    private ArrayList<MonitorTaskSuite> monitoringTasks = new ArrayList<MonitorTaskSuite>();

    /**
     * Instantiates a new monitoring task request handler.
     */
    private MonitoringTaskRequestHandler()
    {
        try
        {
            initailizeServerMonitoringThreadPool();
        }
        catch ( Exception e )
        {
            logger.error( "Error while initializing monitoring ThreadPool.", e );
        }
    }

    /**
     * Inits the.
     *
     * @param threadPoolSize the thread pool size
     */
    public static void init( int threadPoolSize )
    {
        MONITORING_THREAD_POOL_SIZE = threadPoolSize;
    }

    /**
     * Gets the single instance of MonitoringTaskRequestHandler.
     *
     * @return single instance of MonitoringTaskRequestHandler
     */
    public static MonitoringTaskRequestHandler getInstance()
    {
        return instance;
    }

    /**
     * Initailize server monitoring thread pool.
     *
     * @throws ExecutionException the execution exception
     * @throws InterruptedException the interrupted exception
     */
    public void initailizeServerMonitoringThreadPool()
        throws ExecutionException, InterruptedException
    {
        executorService = Executors.newFixedThreadPool( MONITORING_THREAD_POOL_SIZE );
        completionService = new ExecutorCompletionService<MonitoringTaskResponse>( executorService );
    }

    /**
     * Execute server monitor task.
     *
     * @param task the task
     * @throws Exception the exception
     */
    public void executeServerMonitorTask( MonitorTaskSuite task )
        throws Exception
    {
        completionService.submit( task );
        monitoringTasks.add( task );
    }

    /**
     * Shuts monitoring.
     *
     * @param timeoutInMilliSeconds
     *            <p>
     *            Timeout in milli seconds for waiting till all the monitoring threads have been terminated. Typically
     *            this value is the monitoring frequency.
     */
    public void shutMonitoring( Long timeoutInMilliSeconds )
    {
        if ( executorService != null )
        {
            /*
             * For all the MonitorTaskSuite instances, set stopMonitoring to true.
             */
            for ( MonitorTaskSuite monitoringTask : monitoringTasks )
            {
                monitoringTask.setStopMonitoring( true );
            }
            boolean terminated = false;
            executorService.shutdown();
            try
            {
                logger.debug( "Waiting for {} seconds for all the monitoring threads are stopped.",
                              ( timeoutInMilliSeconds ) / 1000 );
                terminated = executorService.awaitTermination( timeoutInMilliSeconds, TimeUnit.MILLISECONDS );
                logger.debug( "Finished waiting for all the monitoring threads to get stopped. "
                    + "[All monitoring threads stopped ?: {} ].", terminated );
            }
            catch ( InterruptedException e )
            {
                logger.error( "Error while awaiting for terminating monitoring threads.", e );
            }
            if ( !terminated || !executorService.isTerminated() )
            {
                logger.debug( "All the monitoring threads have not stopped. Retrying to stop them." );
                executorService.shutdownNow();
            }
            else
            {
                logger.debug( "Successfully stopped all the monitoring threads." );
            }
            executorService = null;
            completionService = null;
        }
    }

    /**
     * Restart monitoring.
     *
     * @param timeoutInMilliSeconds the timeout in milli seconds
     * @throws ExecutionException the execution exception
     * @throws InterruptedException the interrupted exception
     */
    public void restratMonitoring( Long timeoutInMilliSeconds )
        throws ExecutionException, InterruptedException
    {
        shutMonitoring( timeoutInMilliSeconds );
        for ( MonitorTaskSuite task : monitoringTasks )
        {
            completionService.submit( task );
        }
    }

    /**
     * Destroy.
     */
    public void destroy()
    {
        executorService.shutdownNow();
    }
}
