/* ********************************************************************************
 * HmsLocalComponentEventRestService.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.controller;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.aggregator.EventGeneratorTask;
import com.vmware.vrack.hms.aggregator.HealthMonitorEventAggregatorTask;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;

/**
 * @author Yagnesh Chawda Controller for on demand component events on hms-local side
 */
@Controller
@RequestMapping( "/event/host" )
public class HmsLocalComponentEventRestService
{
    private Logger logger = Logger.getLogger( HmsLocalComponentEventRestService.class );

    /**
     * Returns you On-Demand Events list for given node, EventSource, component_id
     *
     * @param host_id
     * @param event_source
     * @param component_id
     * @return List<Event>
     * @throws HmsException
     */
    @RequestMapping( value = "/{host_id}/{event_source}", method = RequestMethod.GET )
    @ResponseBody
    public List<Event> getComponentEvents( @PathVariable( "host_id" ) String host_id,
                                           @PathVariable( "event_source" ) EventComponent event_source)
                                               throws HmsException
    {
        if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            logger.debug( "trying to get Events for Host:" + host_id + " for component: " + event_source );
            ServerNode node = (ServerNode) InventoryLoader.getInstance().getNodeMap().get( host_id );
            try
            {
                ServerComponent component = EventMonitoringSubscriptionHolder.getMappedServerComponents( event_source );
                EventGeneratorTask eventGenerator = new EventGeneratorTask();
                return eventGenerator.getAggregatedEvents( node, component );
            }
            catch ( HmsException e )
            {
                logger.error( "HMSException. Error getting sensor events for node :" + host_id + " component: "
                    + event_source, e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            catch ( Exception e )
            {
                logger.error( "Exception. Error getting sensor events for node :" + host_id + " component: "
                    + event_source, e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
        }
    }

    /**
     * Returns you On-Demand Health Events for HMS
     *
     * @return List<Event>
     * @throws HmsException
     */
    @RequestMapping( value = "/HMS", method = RequestMethod.GET )
    @ResponseBody
    public List<Event> getComponnetEvents()
        throws HmsException
    {
        try
        {
            ServerNode node = InventoryLoader.getInstance().getApplicationNode();
            HealthMonitorEventAggregatorTask eventAggregator = new HealthMonitorEventAggregatorTask();
            return eventAggregator.getAggregatedEvents( node, ServerComponent.HMS );
        }
        catch ( HmsException e )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
        catch ( Exception e )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
    }
}
