/* ********************************************************************************
 * TaskRequestHandler.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms;

import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.task.IHmsTask;
import com.vmware.vrack.hms.task.TaskFactory;
import com.vmware.vrack.hms.task.TaskType;

@Deprecated
public class TaskRequestHandler
{
    private static Logger logger = Logger.getLogger( TaskRequestHandler.class );

    private int THREAD_POOL_SIZE;

    private int MONITORING_THREAD_POOL_SIZE;

    private static volatile TaskRequestHandler instance = new TaskRequestHandler();

    private ExecutorService executor = null;

    private ExecutorService monitor_executor = null;

    public CompletionService<TaskResponse> taskCompService = null;

    public CompletionService<TaskResponse> monitorCompService = null;

    private ArrayList<IHmsTask> monitoringTasks = new ArrayList<IHmsTask>();

    private TaskRequestHandler()
    {
        try
        {
            THREAD_POOL_SIZE =
                Integer.parseInt( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, "THREAD_POOL_SIZE" ) );
            MONITORING_THREAD_POOL_SIZE =
                Integer.parseInt( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS,
                                                               "MONITORING_THREAD_POOL_SIZE" ) );
            initailizeServerThreadPool();
            initailizeMonitoringThreadPool();
        }
        catch ( Exception e )
        {
            logger.error( e.getMessage() );
        }
    }

    public static TaskRequestHandler getInstance()
    {
        return instance;
    }

    public void initailizeServerThreadPool()
        throws ExecutionException, InterruptedException
    {
        executor = Executors.newFixedThreadPool( THREAD_POOL_SIZE );
        taskCompService = new ExecutorCompletionService<TaskResponse>( executor );
    }

    public void initailizeMonitoringThreadPool()
        throws ExecutionException, InterruptedException
    {
        monitor_executor = Executors.newFixedThreadPool( MONITORING_THREAD_POOL_SIZE );
        monitorCompService = new ExecutorCompletionService<TaskResponse>( monitor_executor );
    }

    public void executeServerTask( TaskType type, TaskResponse node )
        throws Exception
    {
        IHmsTask task = TaskFactory.getTask( type, node );
        if ( task != null )
        {
            taskCompService.submit( task );
        }
    }

    public void executeMonitorTask( TaskType type, TaskResponse node )
        throws Exception
    {
        IHmsTask task = TaskFactory.getTask( type, node );
        if ( task != null )
        {
            monitorCompService.submit( task );
            monitoringTasks.add( task );
        }
    }

    public void shutMonitoring()
    {
        if ( monitor_executor != null )
        {
            while ( !monitor_executor.isTerminated() )
                monitor_executor.shutdownNow();
            monitor_executor = null;
            monitorCompService = null;
        }
    }

    public void restratMonitoring()
        throws ExecutionException, InterruptedException
    {
        shutMonitoring();
        initailizeMonitoringThreadPool();
        for ( IHmsTask task : monitoringTasks )
        {
            monitorCompService.submit( task );
        }
    }

    public void destroy()
    {
        executor.shutdownNow();
    }
}
