/* ********************************************************************************
 * ServiceManager.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskRequestHandler;
import com.vmware.vrack.hms.common.service.ServiceState;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;
import com.vmware.vrack.hms.utils.JettyMonitorUtil;

/**
 * <code>ServiceManager</code> class will put service under MAINTENANCE.<br>
 *
 * @author VMware, Inc.
 */
public class ServiceManager
{

    /** The service status. */
    private static ServiceState serviceState = ServiceState.RUNNING;

    /** The active requests. */
    private static int activeRequests;

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger( ServiceManager.class );

    /** The Constant serverNodeConnector. */
    private static final ServerNodeConnector serverNodeConnector = ServerNodeConnector.getInstance();

    /** The Constant switchNodeConnector. */
    private static final SwitchNodeConnector switchNodeConnector = SwitchNodeConnector.getInstance();

    /**
     * Gets the service status.
     *
     * @return the service state
     */
    public static ServiceState getServiceState()
    {

        return serviceState;
    }

    /**
     * Sets the service status.
     *
     * @param serviceState the new service status
     */
    public static void setServiceState( ServiceState serviceState )
    {

        ServiceManager.serviceState = serviceState;
    }

    /**
     * Gets the active requests.
     *
     * @return the active requests
     */
    public static int getActiveRequests()
    {
        return activeRequests;
    }

    /**
     * Sets the active requests.
     *
     * @param activeRequests the new active requests
     */
    public static void setActiveRequests( int activeRequests )
    {
        ServiceManager.activeRequests = activeRequests;
    }

    /**
     * Puts service in maintenance state. Before putting the service in maintenance, all the monitoring threads are
     * shutdown and will wait for a maximum period of 5 minutes for the server to respond to active requests.
     *
     * @return true, if server is put under maintenance successfully. Otherwise, returns false;
     */
    public static boolean putServiceInMaintenance()
    {

        if ( !ServiceManager.serviceState.equals( ServiceState.NORMAL_MAINTENANCE ) )
        {

            // put service in NORMAL_MAINTENANCE first
            logger.info( "Setting service in {} mode.", ServiceState.NORMAL_MAINTENANCE.toString() );
            ServiceManager.setServiceState( ServiceState.NORMAL_MAINTENANCE );

            try
            {

                // stop monitoring
                Long monitoringFrequency =
                    Long.parseLong( HmsConfigHolder.getHMSConfigProperty( "HOST_NODE_MONITOR_FREQUENCY" ) );
                Long additionalWaitTime =
                    Long.parseLong( HmsConfigHolder.getHMSConfigProperty( "SHUTDOWN_MONITORING_ADDITIONAL_WAITITME" ) );

                MonitoringTaskRequestHandler.getInstance().shutMonitoring( monitoringFrequency + additionalWaitTime );

                int maxWaitTime =
                    Integer.parseInt( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS,
                                                                   HmsConfigHolder.HMS_SERVICE_MAINTENANCE_MAX_WAIT_TIME ) );
                int retryInterval =
                    Integer.parseInt( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS,
                                                                   HmsConfigHolder.HMS_SERVICE_MAINTENANCE_RETRY_INTERVAL ) );
                int maxRetries = ( maxWaitTime / retryInterval );

                int retryCount = 0;
                int activeRequestsCount = 0;
                while ( retryCount < maxRetries )
                {

                    activeRequestsCount = JettyMonitorUtil.getActiveRequestsCount();

                    // no active request should in pending
                    if ( activeRequestsCount == 1 )
                    {

                        break;
                    }

                    logger.info( "{} active requests under process by the server.", activeRequestsCount );
                    logger.debug( "Sleeping for {} seconds before checking pending requests.", retryInterval / 1000 );

                    try
                    {

                        Thread.sleep( retryInterval );
                        retryCount++;

                    }
                    catch ( InterruptedException e )
                    {

                        logger.warn( "Error while sleeping for {} seconds before checking pending requests.",
                                     retryInterval / 1000, e );
                    }
                }

                ServiceManager.setActiveRequests( activeRequestsCount );
                if ( activeRequestsCount > 1 )
                {

                    logger.warn( "Even after waiting for {} seconds, {} requests are pending.", ( maxWaitTime / 1000 ),
                                 activeRequestsCount );

                    // change Service to FORCE_MAINTENANCE
                    logger.info( "Setting service in {} mode.", ServiceState.FORCE_MAINTENANCE.toString() );
                    ServiceManager.setServiceState( ServiceState.FORCE_MAINTENANCE );

                }
                else
                {

                    logger.info( "Active requests under process by the server are completed." );
                }
                return true;

            }
            catch ( Exception e )
            {

                logger.error( "Error while setting service in either NORMAL_MAINTENANCE or FORCE_MAINTENANCE.", e );

                // put back service in running state
                ServiceManager.setServiceState( ServiceState.RUNNING );
                ServiceManager.setActiveRequests( 0 );

                return false;
            }
        }
        return true;
    }

    /**
     * Put service in running.
     *
     * @return true, if successful
     */
    public static boolean putServiceInRunning()
    {

        if ( !ServiceManager.serviceState.equals( ServiceState.RUNNING ) )
        {

            /*
             * Reload Server and Switch Inventory (that starts Monitoring as well) TODO: Check with Suket, if this is
             * the right thing to do for restarting Monitoring.
             */

            try
            {
                // Reset Monitoring thread objects
                MonitoringTaskRequestHandler.getInstance().initailizeServerMonitoringThreadPool();
                serverNodeConnector.parseRackInventoryConfig();

            }
            catch ( Exception e )
            {
                ServerNodeConnector.notifyHMSFailure( "ERROR_RESTARTING_MONITORING_AFTER_UPGRADE_FAILURE",
                                                      e.getMessage() );
                logger.error( e.getMessage() );
                return false;
            }

            // TODO: Check with Suket, if this is needed.
            switchNodeConnector.parseRackInventoryConfig();
            switchNodeConnector.initSwitchMonitoring();

            // reset activeRequests count to zero
            ServiceManager.setActiveRequests( 0 );

            // set ServiceState to RUNNING
            ServiceManager.setServiceState( ServiceState.RUNNING );
        }
        return true;
    }
}
