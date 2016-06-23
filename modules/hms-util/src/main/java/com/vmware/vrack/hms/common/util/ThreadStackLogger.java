/* ********************************************************************************
 * ThreadStackLogger.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;

import org.apache.log4j.Logger;

public class ThreadStackLogger
    implements Runnable
{
    private Logger logger;

    private static ThreadStackLogger shutdownThreadStackLogger;

    private static Thread shutdownThread;

    public ThreadStackLogger()
    {
        this( Logger.getLogger( ThreadStackLogger.class ) );
    }

    public ThreadStackLogger( Logger logger )
    {
        this.logger = logger;
    }

    /**
     * Enable thread stack logger with JVM shutdown hook
     */
    public static synchronized void enableShutdownHook()
    {
        if ( shutdownThread == null )
        {
            shutdownThreadStackLogger = new ThreadStackLogger();
            shutdownThread = new Thread( shutdownThreadStackLogger, "thread-stack-logger" );
            shutdownThreadStackLogger.logger.info( "Registering ThreadStackLogger with the shutdown hook ..." );
            Runtime.getRuntime().addShutdownHook( shutdownThread );
        }
    }

    /**
     * Disable thread stack logger with JVM shutdown hook
     */
    public static synchronized void disableShutdownHook()
    {
        if ( shutdownThread != null )
        {
            shutdownThreadStackLogger.logger.info( "De-registering ThreadStackLogger with the shutdown hook ..." );
            Runtime.getRuntime().removeShutdownHook( shutdownThread );
            shutdownThreadStackLogger = null;
            shutdownThread = null;
        }
    }

    @Override
    public void run()
    {
        log();
    }

    /**
     * Log all threads' stack trace and other useful information to logger.
     */
    public String log()
    {
        String logInfo = execute();
        logger.info( logInfo );
        return logInfo;
    }

    public String execute()
    {
        StringBuffer threadDump = new StringBuffer();
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        threadDump.append( "Logging threads and stack trace, thread count = " + allStackTraces.size() );
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        boolean isCpuInfoAvailable = threadMxBean.isThreadCpuTimeSupported();
        for ( Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet() )
        {
            Thread t = entry.getKey();
            threadDump.append( "\nThread: \"" + t.getName() + "\"" );
            threadDump.append( t.isDaemon() ? " daemon" : "" );
            threadDump.append( isCpuInfoAvailable
                            ? " cpu=" + threadMxBean.getThreadCpuTime( t.getId() ) / ( 1000.0 * 1000.0 ) + "ms" : "" );
            threadDump.append( " prio=" + t.getPriority() );
            threadDump.append( " state=" + t.getState() );
            threadDump.append( "\n" );
            for ( StackTraceElement s : entry.getValue() )
            {
                threadDump.append( "    at " + s.toString() + "\n" );
            }
        }
        return ( threadDump.toString() );
    }
}
