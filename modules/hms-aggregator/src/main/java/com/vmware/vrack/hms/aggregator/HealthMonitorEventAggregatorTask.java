/* ********************************************************************************
 * HealthMonitorEventAggregatorTask.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Body;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.util.HMSLocalHealthSensor;
import com.vmware.vrack.hms.aggregator.util.InventoryUtil;
import com.vmware.vrack.hms.aggregator.util.MonitoringUtil;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.resource.task.jmx.HMSResourceMonitor;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.EventFilterService;

@SuppressWarnings( "deprecation" )
public class HealthMonitorEventAggregatorTask
    implements IEventAggregatorTask
{

    private Logger logger = Logger.getLogger( HealthMonitorEventAggregatorTask.class );

    private static final String NODE = "DUMMY";

    /**
     * Get OOB Events for given node and Component
     *
     * @param node_id
     * @param component
     * @return List<Event>
     */
    private List<Event> getOOBEvents( ServerNode node, ServerComponent component )
    {
        try
        {
            return MonitoringUtil.getHealthMonitorEventsOOB();
        }
        catch ( Exception e )
        {
            return null;
        }

    }

    /**
     * Get IB Events for given node and Component
     *
     * @param node_id
     * @param component
     * @return List<Event>
     */
    private List<Event> getIBEvents( ServerNode node, ServerComponent component )
    {
        try
        {
            executeHealthMonitorTask( node, component );
            return EventMonitoringSubscriptionHolder.getEventList( node, component );
        }
        catch ( Exception e )
        {
            return null;
        }

    }

    /**
     * Get ALL Events (OOB & IB) for given node and Component
     * 
     * @param node_id
     * @param component
     * @return List<Event>
     */
    @Override
    public List<Event> getAggregatedEvents( ServerNode node, ServerComponent component )
        throws HMSRestException
    {

        List<Event> aggregatedEvents = new ArrayList<Event>();
        aggregatedEvents.addAll( getIBEvents( node, component ) );
        aggregatedEvents.addAll( getOOBEvents( node, component ) );

        try
        {
            aggregatedEvents = EventFilterService.filterOrMassageEvents( NODE, component, aggregatedEvents );
        }
        catch ( Exception e )
        {
            // Ignore
            logger.warn( "Error filtering events for HMS:: Component:" + component, e );
        }
        return aggregatedEvents;
    }

    /**
     * Get ALL Events (OOB & IB) for given node and Component & refreshes the inventory on Out of band if it's not
     * already completed. The inventory is already refreshed on Out of band or not is based on the flag
     * hms.inventory.availability.status returned.
     *
     * @param node_id
     * @param component
     * @return List<Event>
     */
    @Override
    public List<Event> processEvents( ServerNode node, ServerComponent component )
        throws HMSRestException
    {

        List<Event> aggregatedEvents = new ArrayList<Event>();
        aggregatedEvents.addAll( getIBEvents( node, component ) );
        List<Event> oobEvents = getOOBEvents( node, component );
        aggregatedEvents.addAll( oobEvents );

        initiateInventoryRefreshOnOutOfBand( oobEvents );

        try
        {
            aggregatedEvents = EventFilterService.filterOrMassageEvents( NODE, component, aggregatedEvents );
        }
        catch ( Exception e )
        {
            // Ignore
            logger.warn( "Error filtering events for HMS:: Component:" + component, e );
        }
        return aggregatedEvents;
    }

    /**
     * Initiates inventory refresh on Out of band
     *
     * @param oobEvents
     */
    private void initiateInventoryRefreshOnOutOfBand( List<Event> oobEvents )
    {
        if ( oobEvents != null && oobEvents.size() > 0 )
        {
            Event event = oobEvents.get( 0 );
            Body body = event.getBody();
            if ( body != null )
            {
                Map<String, String> data = body.getData();
                if ( data != null )
                {
                    String status = data.get( Constants.HMS_INV_FROM_AGG_AVAILABILITY_STATUS );
                    if ( status != null )
                    {
                        if ( Boolean.valueOf( status ) )
                        {
                            logger.info( "Inventory is already available at oobagent" );
                            return;
                        }
                    }
                }
            }
        }

        InventoryUtil.refreshInventoryOnOutOfBand();
    }

    /**
     * Execute HmsLocalMonitorTask for the given Server Node
     *
     * @param node
     * @param component
     * @return
     * @throws HMSRestException
     */
    private void executeHealthMonitorTask( ServerNode node, ServerComponent component )
        throws HMSRestException
    {
        try
        {
            IComponentEventInfoProvider boardService = new HMSLocalHealthSensor();
            MonitoringTaskResponse response = new MonitoringTaskResponse( node, component, boardService );
            HMSResourceMonitor task = new HMSResourceMonitor( response );
            task.executeTask();
        }
        catch ( HmsException e )
        {
            logger.error( "Encountered exception during execution of task", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
        catch ( Exception e )
        {
            logger.error( "Encountered exception during execution of task", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }

    }

    /**
     * @param switchId
     * @param component
     * @return List<Event>
     * @throws HMSRestException
     */
    @Override
    public List<Event> getAggregatedSwitchEvents( String switchId, SwitchComponentEnum component )
        throws HMSRestException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> getAggregatedEvents( ServerNode node, ServerComponent component, boolean oobMonitoring,
                                            boolean ibMonitoring )
        throws HMSRestException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Event> getAggregatedSwitchEvents( String switchId, SwitchComponentEnum component,
                                                  boolean switchMonitoring )
        throws HMSRestException
    {
        // TODO Auto-generated method stub
        return null;
    }
}
