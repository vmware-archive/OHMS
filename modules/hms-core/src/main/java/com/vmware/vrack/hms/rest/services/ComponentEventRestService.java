/* ********************************************************************************
 * ComponentEventRestService.java
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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsOperationNotSupportedException;
import com.vmware.vrack.hms.common.monitoring.MonitorSwitchTask;
import com.vmware.vrack.hms.common.monitoring.MonitorTask;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.resource.task.jmx.HMSResourceMonitor;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;
import com.vmware.vrack.hms.utils.HMSOOBHealthSensor;

/**
 * @author sgakhar Controller for on demand component events
 */
@Path( "/event" )
public class ComponentEventRestService
{
    private ServerNodeConnector serverConnector = ServerNodeConnector.getInstance();

    private SwitchNodeConnector switchConnector = SwitchNodeConnector.getInstance();

    private Logger logger = Logger.getLogger( ComponentEventRestService.class );

    /**
     * @param host_id
     * @param event_source
     * @param component_id
     * @return List<Event>
     * @throws HMSRestException
     */
    @GET
    @Path( "/host/{host_id}/{event_source}" )
    @Produces( "application/json" )
    public List<Event> getComponnetEvents( @PathParam( "host_id" ) String host_id,
                                           @PathParam( "event_source" ) EventComponent event_source)
                                               throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            try
            {
                ServerComponent component = EventMonitoringSubscriptionHolder.getMappedServerComponents( event_source );
                executeServerMonitorTask( node, component );
                return EventMonitoringSubscriptionHolder.getEventList( node, component );
            }
            catch ( HmsOperationNotSupportedException e )
            {
                logger.error( "Operation not supported for node :" + host_id + " component: " + event_source );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            catch ( HMSRestException e )
            {
                logger.error( "Error getting sensor events for node :" + host_id + " component: " + event_source, e );
                throw e;
            }
            catch ( HmsException e )
            {
                logger.error( "Error getting sensor events for node :" + host_id + " component: " + event_source, e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            catch ( Exception e )
            {
                logger.error( "Error getting sensor events for node :" + host_id + " component: " + event_source, e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
        }
    }

    @GET
    @Path( "/host/nme/{host_id}" )
    @Produces( "application/json" )
    public List<Event> getComponnetNmeEvents( @PathParam( "host_id" ) String host_id)
        throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            List<Event> events = new ArrayList<Event>();
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            try
            {
                for ( ServerComponent component : ServerComponent.values() )
                {
                    try
                    {
                        executeServerMonitorTask( node, component );
                    }
                    catch ( HmsException e )
                    {
                        logger.error( "Exception occured while executing ServerMonitor task for component" + component,
                                      e );
                    }
                    events.addAll( EventMonitoringSubscriptionHolder.getEventList( node, component, true ) );
                }
                return events;
            }
            catch ( Exception e )
            {
                logger.error( "Error getting sensor events for node :" + host_id, e );
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
    @GET
    @Path( value = "/host/HMS" )
    @Produces( "application/json" )
    public List<Event> getComponentEvents()
        throws HmsException
    {
        ServerNode node = ServerNodeConnector.getInstance().getApplicationNode();
        try
        {
            executeHealthMonitorTask( node, ServerComponent.HMS );
            return EventMonitoringSubscriptionHolder.getEventList( node, ServerComponent.HMS );
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

    @GET
    @Path( "/switches/{switch_id}/{event_source}" )
    @Produces( "application/json" )
    public List<Event> getSwitchComponentEvents( @PathParam( "switch_id" ) String switchId,
                                                 @PathParam( "event_source" ) EventComponent eventSource)
                                                     throws HMSRestException
    {
        if ( !switchConnector.contains( switchId ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find switch with id " + switchId );
        else
        {
            SwitchNode switchNode = switchConnector.getSwitchNode( switchId );
            HmsNode node = new HMSSwitchNode( switchNode.getSwitchId(), switchNode.getIpAddress(),
                                              switchNode.getUsername(), switchNode.getPassword() );
            try
            {
                SwitchComponentEnum component =
                    EventMonitoringSubscriptionHolder.getMappedSwitchComponents( eventSource );
                executeSwitchMonitorTask( node, component );
                return EventMonitoringSubscriptionHolder.getSwitchEventList( node, component );
            }
            catch ( Exception e )
            {
                logger.error( "Error getting sensor events for switch " + switchId + " component " + eventSource, e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
        }
    }

    @GET
    @Path( "/switches/nme/{switch_id}" )
    @Produces( "application/json" )
    public List<Event> getSwitchComponentNmeEvents( @PathParam( "switch_id" ) String switchId)
        throws HMSRestException
    {
        if ( !switchConnector.contains( switchId ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find switch with id " + switchId );
        else
        {
            List<Event> events = new ArrayList<Event>();
            SwitchNode switchNode = switchConnector.getSwitchNode( switchId );
            HmsNode node = new HMSSwitchNode( switchNode.getSwitchId(), switchNode.getIpAddress(),
                                              switchNode.getUsername(), switchNode.getPassword() );
            for ( SwitchComponentEnum component : SwitchComponentEnum.values() )
            {
                try
                {
                    executeSwitchMonitorTask( node, component );
                    events.addAll( EventMonitoringSubscriptionHolder.getSwitchEventList( node, component, true ) );
                }
                catch ( Exception e )
                {
                    logger.error( "Error getting sensor events for switch " + switchId + " for component:"
                        + component );
                }
            }
            return events;
        }
    }

    /**
     * @param node
     * @param component
     * @return
     * @throws HMSRestException
     */
    private void executeServerMonitorTask( ServerNode node, ServerComponent component )
        throws HMSRestException, HmsOperationNotSupportedException
    {
        try
        {
            IBoardService boardService = BoardServiceProvider.getBoardService( node.getServiceObject() );
            MonitoringTaskResponse response = new MonitoringTaskResponse( node, component, boardService );
            MonitorTask task = new MonitorTask( response );
            task.executeTask();
        }
        catch ( HmsOperationNotSupportedException e )
        {
            logger.error( "Operation is not supported for BoardService", e );
            throw e;
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
     * @param node
     * @param component
     * @return
     * @throws HMSRestException
     */
    private void executeSwitchMonitorTask( HmsNode node, SwitchComponentEnum component )
        throws HMSRestException
    {
        try
        {
            ISwitchService service = switchConnector.getSwitchService( node.getNodeID() );
            MonitoringTaskResponse response = new MonitoringTaskResponse( node, component, service );
            MonitorSwitchTask task = new MonitorSwitchTask( response );
            task.executeTask();
        }
        catch ( Exception e )
        {
            logger.error( "Encountered exception during execution of monitoring task", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
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
            IComponentEventInfoProvider boardService = new HMSOOBHealthSensor();
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
}
