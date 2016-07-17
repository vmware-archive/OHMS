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
package com.vmware.vrack.hms.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.aggregator.util.InventoryUtil;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskRequestHandler;
import com.vmware.vrack.hms.common.service.ServiceState;
import com.vmware.vrack.hms.controller.MaintenanceInterceptor;

/**
 * <code>ServiceManager</code><br>
 *
 * @author VMware, Inc.
 */
public class ServiceManager
{
    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger( ServiceManager.class );

    /** The service status. */
    private static ServiceState serviceState = ServiceState.RUNNING;

    /**
     * Put service in maintenance.
     *
     * @param shutdownMonitoringMaxWaittime the shutdown monitoring max waittime
     * @param maxWaitTime the max wait time
     * @param retryInterval the retry interval
     * @return true, if successful
     */
    public static boolean putServiceInMaintenance( final Long shutdownMonitoringMaxWaittime, final int maxWaitTime,
                                                   final int retryInterval )
    {
        if ( ( ServiceManager.getServiceState() == ServiceState.NORMAL_MAINTENANCE )
            || ( ServiceManager.getServiceState() == ServiceState.FORCE_MAINTENANCE ) )
        {
            logger.info( "HMS Service is already in {} state.", ServiceManager.getServiceState().toString() );
            return true;
        }
        // first put service in NORMAL_MAINTENANCE
        ServiceManager.setServiceState( ServiceState.NORMAL_MAINTENANCE );
        // STEP#1: STOP MONITORING
        MonitoringTaskRequestHandler.getInstance().shutMonitoring( shutdownMonitoringMaxWaittime );
        try
        {
            int maxRetries = maxWaitTime / retryInterval;
            int retryCount = 0;
            int activeRequestsCount = 0;
            while ( retryCount < maxRetries )
            {
                activeRequestsCount = MaintenanceInterceptor.getActiveRequests();
                // no active request should in pending
                if ( activeRequestsCount == 0 )
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
            if ( activeRequestsCount > 0 )
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
            return false;
        }
    }

    /**
     * Sets the service in running.
     *
     * @param hmsIbInventoryLocation the hms ib inventory location
     * @param oobHost the oob host
     * @param oobPort the oob port
     * @param oobNodesEndpoint the oob node endpoint
     * @return true, if successful
     */
    public static boolean setServiceInRunning( final String hmsIbInventoryLocation, final String oobHost,
                                               final int oobPort, final String oobNodesEndpoint )
    {
        boolean inventoryLoaded = false;
        try
        {
            // initialize inventory also starts monitoring for all nodes
            inventoryLoaded =
                InventoryUtil.initializeInventory( hmsIbInventoryLocation, oobHost, oobPort, oobNodesEndpoint );
        }
        catch ( HmsException e )
        {
            logger.error( "Error while loading inventory and starting monitoring." );
        }
        if ( !inventoryLoaded )
        {
            logger.error( "Unable to load inventory and start monitoring to put service in running." );
        }
        else
        {
            // put service in running state
            ServiceManager.setServiceState( ServiceState.RUNNING );
            logger.info( "Successfully put service in RUNNING after loading inventory and starting monitoring." );
            return true;
        }
        return false;
    }

    /**
     * @return the serviceState
     */
    public static ServiceState getServiceState()
    {
        return serviceState;
    }

    /**
     * @param serviceState the serviceState to set
     */
    public static void setServiceState( ServiceState serviceState )
    {
        ServiceManager.serviceState = serviceState;
    }
}
