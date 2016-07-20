/* ********************************************************************************
 * InventoryRestService.java
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
package com.vmware.vrack.hms.rest.services;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.configuration.HmsInventoryConfiguration;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskRequestHandler;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;

/**
 * <code>InventoryRestService</code><br>
 *
 * @author VMware, Inc.
 */
@Path( "/inventory" )
public class InventoryRestService
{
    /** The logger. */
    private Logger logger = LoggerFactory.getLogger( InventoryRestService.class );

    /** The server connector. */
    private ServerNodeConnector serverConnector = ServerNodeConnector.getInstance();

    /** The switch connector. */
    private SwitchNodeConnector switchConnector = SwitchNodeConnector.getInstance();

    /**
     * Reloads inventory.
     *
     * @param hic HmsInventoryConfiguration
     * @return the base response
     * @throws HMSRestException the HMS rest exception
     */
    @PUT
    @Path( "/reload" )
    @Produces( "application/json" )
    public BaseResponse reloadInventory( HmsInventoryConfiguration hic )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        try
        {
            logger.debug( "Received HMS inventory configuration." );
            if ( hic != null )
            {
                HmsConfigHolder.setHmsInventoryConfiguration( hic );
                Long monitoringFrequency =
                    Long.parseLong( HmsConfigHolder.getHMSConfigProperty( "HOST_NODE_MONITOR_FREQUENCY" ) );
                Long additionalWaitTime =
                    Long.parseLong( HmsConfigHolder.getHMSConfigProperty( "SHUTDOWN_MONITORING_ADDITIONAL_WAITITME" ) );
                MonitoringTaskRequestHandler.getInstance().shutMonitoring( monitoringFrequency + additionalWaitTime );
                serverConnector.parseRackInventoryConfig();
                // Re-parse switch configuration
                switchConnector.parseRackInventoryConfig();
                logger.debug( "Successfully set HMS Inventory config file." );
            }
            else
            {
                String err = "Unable to set Inventory in HMS OOB, as HmsInventoryConfig is null.";
                logger.error( err );
                response.setErrorMessage( err );
                response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
                return response;
            }
        }
        catch ( Exception e )
        {
            logger.error( "Exception received while re-parsing HMS inventory configuration file.", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
        response.setStatusCode( Status.ACCEPTED.getStatusCode() );
        response.setStatusMessage( "Requested operation triggered successfully." );
        return response;
    }
}
