/* ********************************************************************************
 * HmsApp.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.vmware.vrack.hms.common.util.ThreadStackLogger;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;

/**
 * Main entry point for HMS application.
 */
public class HmsApp
{
    public static final int SHUTDOWN_HMS = 52;

    public static final int RESTART_HMS = 62;

    public static int HMS_EXIT_CODE = SHUTDOWN_HMS;

    private static Logger logger = Logger.getLogger( HmsApp.class );

    public static void main( String[] args )
    {
        try
        {
            ThreadStackLogger.enableShutdownHook();
            @SuppressWarnings( "unused" )
            ApplicationContext context = new ClassPathXmlApplicationContext( "hms-config.xml" );
        }
        catch ( Exception e )
        {
            logger.error( "Shutting down HMS application due to exception", e );
            ServerNodeConnector.notifyHMSFailure( "APP_INITIALIZATION_ERROR", "Shutting down HMS : " + e.getMessage() );
            System.exit( SHUTDOWN_HMS );
        }
    }
}
