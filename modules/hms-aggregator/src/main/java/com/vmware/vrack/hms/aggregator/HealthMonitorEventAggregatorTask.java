/* ********************************************************************************
 * HealthMonitorEventAggregatorTask.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.aggregator;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.hms.aggregator.util.HMSLocalHealthSensor;
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
import com.vmware.vrack.hms.common.util.EventFilterService;

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
}
