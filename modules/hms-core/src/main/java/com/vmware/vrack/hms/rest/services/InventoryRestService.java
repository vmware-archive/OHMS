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

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.HmsGenericUtil;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;
import com.vmware.vrack.hms.utils.OobUtil;

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
     * API to be consumed by Aggregator for inventory refresh
     *
     * @param inventoryMap
     * @return the base response
     * @throws HMSRestException the HMS rest exception
     */
    @PUT
    @Path( "/reload" )
    @Produces( "application/json" )
    public BaseResponse reloadInventory( Map<String, Object[]> inventoryMap )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        String msg = null;

        try
        {

            if ( inventoryMap == null || inventoryMap.size() == 0 )
            {
                msg = "Invalid inventory. Inventory is either null or blank.";
                response = HmsGenericUtil.getBaseResponse( Status.BAD_REQUEST, msg );
                return response;
            }

            logger.debug( "Received HMS inventory configuration." );

            if ( !HmsConfigHolder.isHmsInventoryFileExists() )
            {

                List<ServerNode> serverNodes = OobUtil.extractServerNodes( inventoryMap );
                List<SwitchNode> switchNodes = OobUtil.extractSwitchNodes( inventoryMap );

                serverConnector.executeServerNodeRefresh( serverNodes );
                switchConnector.executeSwitchNodeRefresh( switchNodes );

                HmsConfigHolder.setInvRefreshedFromAggregator( true );

                logger.debug( "Successfully set HMS Inventory config file." );

            }
            else
            {
                logger.warn( "Inventory file already exists under config, so not reloading the inventory" );
            }

        }
        catch ( Exception e )
        {
            msg = "Exception received while loading HMS inventory configuration.";
            logger.error( msg, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), msg, e.getMessage() );
        }

        msg = "Requested operation triggered successfully.";
        response = HmsGenericUtil.getBaseResponse( Status.ACCEPTED, msg );

        return response;
    }

    /**
     * This method will specify if the aggregator's inventory is loaded to Out of band or not
     *
     * @return
     * @throws HMSRestException
     */
    @GET
    @Path( "/isloaded" )
    @Produces( "application/json" )
    public boolean isAggregatorInventoryLoadedToOutOfBand()
        throws HMSRestException
    {
        return HmsConfigHolder.isInvRefreshedFromAggregator();
    }
}
