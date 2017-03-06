/* ********************************************************************************
 * ThreadStackLogger.java
 * 
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadStackLogger
    implements Runnable
{
    private Logger logger;

    private static ThreadStackLogger shutdownThreadStackLogger;

    private static Thread shutdownThread;

    public ThreadStackLogger()
    {
        this( LoggerFactory.getLogger( ThreadStackLogger.class ) );
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
