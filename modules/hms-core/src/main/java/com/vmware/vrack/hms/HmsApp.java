/* ********************************************************************************
 * HmsApp.java
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
