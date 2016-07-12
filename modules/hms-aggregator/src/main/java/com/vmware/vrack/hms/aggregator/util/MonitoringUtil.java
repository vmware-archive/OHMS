/* ********************************************************************************
 * MonitoringUtil.java
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
package com.vmware.vrack.hms.aggregator.util;

import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.enums.EventCatalog;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.common.event.util.EventFactory;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentEventInfoProvider;
import com.vmware.vrack.hms.common.boardvendorservice.api.IComponentSwitchEventInfoProvider;
import com.vmware.vrack.hms.common.events.BaseEventMonitoringSubscription;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskRequestHandler;
import com.vmware.vrack.hms.common.monitoring.MonitoringTaskResponse;
import com.vmware.vrack.hms.common.resource.AboutResponse;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.event.EventUnitType;
import com.vmware.vrack.hms.common.servernodes.api.event.NodeEvent;
import com.vmware.vrack.hms.common.switchnodes.api.HMSSwitchNode;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.NetworkInterfaceUtil;
import com.vmware.vrack.hms.controller.HMSLocalServerRestService;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

/**
 * @author Yagnesh Chawda
 */
@Component
public class MonitoringUtil
{
    private static Logger logger = Logger.getLogger( MonitoringUtil.class );

    private static String hmsIpAddr;

    private static String hmsLocalPort;

    private static String hmsLocalContext;

    private static String hmsLocalProtocol;

    private static HMSLocalServerRestService hmsLocalServerRestService;

    private static final String HMS_AGENT_STATUS_DESC_SEPARATOR = " -";

    /**
     * Contains all the nodes, which are currently being monitored.
     */
    private static Set<String> monitoredNodes = new HashSet<String>();

    /**
     * Flag to check if the HMS Health Monitoring is already started
     */
    private static boolean healthMonitoringStarted = false;

    /**
     * Monitoring frequency If the frequency is not explicitly mentioned, it will try to monitor every 2 minutes(120000
     * ms).
     */
    private static long monitoringFrequency;

    // Enable or disable Monitoring on HMS-Local by making setting true/false
    private static String enableMonitoring;

    private static int hmsPort;

    /**
     * Network interface name to which hms OOB will send data to hms IB
     */
    private static String hmsIbNetworkInterface;

    public static String getHmsIbNetworkInterface()
    {
        return hmsIbNetworkInterface;
    }

    @Value( "${hms.network.interface.1}" )
    public void setHmsIbNetworkInterface( String hmsIbNetworkInterface )
    {
        MonitoringUtil.hmsIbNetworkInterface = hmsIbNetworkInterface;
    }

    @Value( "${hms.switch.host}" )
    public void setHmsIpAddr( String hmsIpAddr )
    {
        MonitoringUtil.hmsIpAddr = hmsIpAddr;
    }

    @Value( "${hms.switch.port}" )
    public void setHmsPort( int hmsPort )
    {
        MonitoringUtil.hmsPort = hmsPort;
    }

    public static boolean isMonitoringEnabled()
    {
        if ( "true".equalsIgnoreCase( MonitoringUtil.enableMonitoring ) )
        {
            return true;
        }
        return false;
    }

    @Value( "${enable.monitoring:false}" )
    public void setEnableMonitoring( String enableMonitoring )
    {
        if ( "true".equalsIgnoreCase( enableMonitoring ) )
        {
            MonitoringUtil.enableMonitoring = "true";
        }
        else
        {
            MonitoringUtil.enableMonitoring = "false";
        }
    }

    /**
     * Gets the Hms-local Ip address by defined Network Interface Name
     */
    public static String getHmsLocalIP()
        throws SocketException
    {
        return NetworkInterfaceUtil.getByInterfaceName( hmsIbNetworkInterface );
    }

    public static String getHmsLocalPort()
    {
        return hmsLocalPort;
    }

    @Value( "${hms.local.port}" )
    public void setHmsLocalPort( String hmsLocalPort )
    {
        MonitoringUtil.hmsLocalPort = hmsLocalPort;
    }

    public static String getHmsLocalContext()
    {
        return hmsLocalContext;
    }

    @Value( "${hms.local.context}" )
    public void setHmsLocalContext( String hmsLocalContext )
    {
        MonitoringUtil.hmsLocalContext = hmsLocalContext;
    }

    public static String getHmsLocalProtocol()
    {
        return hmsLocalProtocol;
    }

    @Value( "${hms.local.protocol}" )
    public void setHmsLocalProtocol( String hmsLocalProtocol )
    {
        MonitoringUtil.hmsLocalProtocol = hmsLocalProtocol;
    }

    @Autowired
    public void setHmsLocalServerRestService( HMSLocalServerRestService hmsLocalServerRestService )
    {
        MonitoringUtil.hmsLocalServerRestService = hmsLocalServerRestService;
    }

    @Value( "${monitor.frequency:120000}" )
    public void setMonitoringFrequency( long monitoringFrequency )
    {
        MonitoringUtil.monitoringFrequency = monitoringFrequency;
    }

    /**
     * Start HMS Health Monitoring
     */
    public static void startHMSHealthMonitoring( ServerNode node )
    {
        // Check if Monitoring has been enabled on HMS-local or NOT
        if ( isMonitoringEnabled() )
        {
            if ( node != null )
            {
                if ( !healthMonitoringStarted )
                {
                    try
                    {
                        MonitoringTaskResponse response = getServerTaskResponse( node, null );
                        HmsLocalHealthMonitorTaskSuite healthMonitorTaskSuite =
                            new HmsLocalHealthMonitorTaskSuite( response, monitoringFrequency );
                        MonitoringTaskRequestHandler.getInstance().executeServerMonitorTask( healthMonitorTaskSuite );
                        healthMonitoringStarted = true;
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Error while submitting HMS health monitoring Task", e );
                    }
                }
            }
            else
            {
                logger.error( "Can not start Monitoring for empty node: "
                    + ( ( node != null ) ? node.getNodeID() : null ) );
            }
        }
        else
        {
            logger.warn( "Monitoring Disabled. Not starting HMS health Monitoring" );
        }
    }

    /**
     * Start hms-local side monitoring for particular NODE is it is NOT already getting monitored.
     * 
     * @param node
     */
    public static void startMonitoring( HmsNode node )
    {
        // Check if Monitoring has been enabled on HMS-local or NOT
        if ( isMonitoringEnabled() )
        {
            if ( node != null )
            {
                // First Check if this node is already in the monitoredNode.
                // If it is present, it means that it is already being monitored. In that case it will simply skip
                // following.
                if ( !monitoredNodes.contains( node.getNodeID() ) )
                {
                    /*
                     * Populate Server Components data in node itself, because, it will be needed to perform
                     * getServerComponents on ServerNode
                     */
                    IComponentEventInfoProvider sensorInfoProvider = null;
                    try
                    {
                        MonitoringTaskResponse response = null;
                        logger.debug( "Monitoring server and switch nodes" );
                        if ( node instanceof ServerNode )
                        {
                            // Setting sensorInfoProvider as InbandServiceProvide because this monitoring will be
                            // limited to hms-local
                            sensorInfoProvider = InBandServiceProvider.getBoardService( node.getServiceObject() );
                            response = getServerTaskResponse( node, sensorInfoProvider );
                        }
                        else if ( node instanceof HMSSwitchNode )
                            response = getSwitchTaskResponse( node, null );
                        HmsLocalMonitorTaskSuite monitorTaskSuite =
                            new HmsLocalMonitorTaskSuite( response, monitoringFrequency );
                        MonitoringTaskRequestHandler.getInstance().executeServerMonitorTask( monitorTaskSuite );
                        monitoredNodes.add( node.getNodeID() );
                    }
                    catch ( HmsException e )
                    {
                        logger.error( "Can not start Monitoring for Node: " + node.getNodeID(), e );
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Error while submitting monitoring Task for node: " + node.getNodeID(), e );
                    }
                }
                else
                {
                    logger.error( "Can not start Monitoring for empty node: "
                        + ( ( node != null ) ? node.getNodeID() : null ) );
                }
            }
            else
            {
                logger.warn( "Can not start Monitoring for NULL node" );
            }
        }
        else
        {
            logger.warn( "Monitoring Disabled. Not starting Monitoring for node: "
                + ( ( node != null ) ? node.getNodeID() : null ) );
        }
    }

    /**
     * Returns MonitoringTaskResponse object to start MonitorTaskSuite.
     * 
     * @param node
     * @param sensorInfoProvider
     * @return
     */
    public static MonitoringTaskResponse getServerTaskResponse( HmsNode node,
                                                                IComponentEventInfoProvider sensorInfoProvider )
    {
        MonitoringTaskResponse response =
            new MonitoringTaskResponse( node, getMonitoredComponents(), sensorInfoProvider );
        return response;
    }

    public static MonitoringTaskResponse getSwitchTaskResponse( HmsNode node,
                                                                IComponentSwitchEventInfoProvider sensorInfoProvider )
    {
        MonitoringTaskResponse response =
            new MonitoringTaskResponse( node, getMonitoredSwitchComponents(), sensorInfoProvider );
        return response;
    }

    public static List<ServerComponent> getMonitoredComponents()
    {
        List<ServerComponent> monitoredComponents =
            new ArrayList<ServerComponent>( Arrays.asList( ServerComponent.values() ) );
        return monitoredComponents;
    }

    public static List<SwitchComponentEnum> getMonitoredSwitchComponents()
    {
        List<SwitchComponentEnum> monitoredComponents =
            new ArrayList<SwitchComponentEnum>( Arrays.asList( SwitchComponentEnum.values() ) );
        return monitoredComponents;
    }

    /**
     * Start monitoring for all nodes - Server and Switch Nodes
     *
     * @param data (nodes)
     */
    public static void startMonitoringForAllNodes( Map<String, Object[]> data )
    {
        Collection<ServerNode> serverNodes = null;
        serverNodes = InventoryLoader.getInstance().getNodeMap().values();
        Object[] switches = data.get( Constants.SWITCHES );
        // Monitor Server Nodes
        if ( serverNodes != null )
        {
            for ( ServerNode node : serverNodes )
            {
                startMonitoring( node );
            }
        }
        // Monitor Switch Nodes
        for ( int i = 0; i < switches.length; i++ )
        {
            @SuppressWarnings( "unchecked" )
            Map<String, String> switchMap = (Map<String, String>) switches[i];
            String switchId = switchMap.get( "switchId" );
            String switchManagementIpAddress = switchMap.get( "ipAddress" );
            HmsNode hmsNode = new HMSSwitchNode( switchId, switchManagementIpAddress );
            startMonitoring( hmsNode );
        }
    }

    /**
     * Populate Server Components (CPU, NIC, HDD etc) via Inband Apis
     * 
     * @param node
     */
    public static void populateServerComponents( HmsNode node )
    {
        logger.debug( "Populating Server Components for the node : " + node );
        if ( node != null )
        {
            try
            {
                hmsLocalServerRestService.getCpuInfo( node.getNodeID() );
            }
            catch ( HMSRestException e )
            {
                logger.error( "Error in populating AbstractServerComponents in Server: " + node.getNodeID(), e );
            }
            try
            {
                hmsLocalServerRestService.getHddInfo( node.getNodeID() );
            }
            catch ( HMSRestException e )
            {
                logger.error( "Error in populating AbstractServerComponents in Server: " + node.getNodeID(), e );
            }
            try
            {
                hmsLocalServerRestService.getNicInfo( node.getNodeID() );
            }
            catch ( HMSRestException e )
            {
                logger.error( "Error in populating AbstractServerComponents in Server: " + node.getNodeID(), e );
            }
            try
            {
                hmsLocalServerRestService.getMemoryInfo( node.getNodeID() );
            }
            catch ( HMSRestException e )
            {
                logger.error( "Error in populating AbstractServerComponents in Server: " + node.getNodeID(), e );
            }
        }
    }

    /**
     * Gets On Demand Events from OOB Api.
     * 
     * @param nodeId
     * @param serverComponent
     * @param targetId
     * @return
     * @throws HmsException
     */
    public static List<Event> getOnDemandEventsOOB( String nodeId, ServerComponent serverComponent )
        throws HmsException
    {
        if ( nodeId != null && serverComponent != null )
        {
            URI uri = null;
            try
            {
                uri = new URI( "http", null, hmsIpAddr, hmsPort, Constants.HMS_ON_DEMAND_EVENTS_FETCH_URI + "/" + nodeId
                    + "/" + serverComponent.getEventComponent(), null, null );
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
                HttpEntity<Object> entity = new HttpEntity<Object>( headers );
                ParameterizedTypeReference<List<Event>> typeRef = new ParameterizedTypeReference<List<Event>>()
                {
                };
                ResponseEntity<List<Event>> oobResponse = restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
                if ( oobResponse.getStatusCode() != HttpStatus.OK )
                {
                    throw new HmsException( "Error while getting Response from Hms-core. For node: " + nodeId
                        + ", serverComponent: " + serverComponent + ", with Status code: "
                        + oobResponse.getStatusCode() );
                }
                List<Event> events = oobResponse.getBody();
                logger.debug( "Got Events from HMS-OOB:" + events );
                return events;
            }
            catch ( Exception e )
            {
                throw new HmsException( "Error while getting Response from Hms-core. For node: " + nodeId
                    + ", serverComponent: " + serverComponent, e );
            }
        }
        else
        {
            logger.error( "One of the mandatory inputs while getting events via OOB is NULL. node: " + nodeId
                + ", serverComponent: " + serverComponent );
            throw new HmsException( "Exception while trying to get On Demand Sensor Data." );
        }
    }

    /**
     * Gets On Demand Events from OOB for switch
     *
     * @param switchId
     * @param switchComponent
     * @return List<Event>
     * @throws HmsException
     */
    public static List<Event> getOnDemandSwitchEventsOOB( String switchId, SwitchComponentEnum switchComponent )
        throws HmsException
    {
        if ( switchId != null && switchComponent != null )
        {
            URI uri = null;
            try
            {
                uri = new URI( "http", null, hmsIpAddr, hmsPort, Constants.HMS_ON_DEMAND_SWITCH_EVENTS_FETCH_URI + "/"
                    + switchId + "/" + switchComponent.getEventComponent(), null, null );
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
                HttpEntity<Object> entity = new HttpEntity<Object>( headers );
                ParameterizedTypeReference<List<Event>> typeRef = new ParameterizedTypeReference<List<Event>>()
                {
                };
                ResponseEntity<List<Event>> oobResponse = restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
                if ( oobResponse.getStatusCode() != HttpStatus.OK )
                {
                    throw new HmsException( "Error while getting Response from HMS OOB agent. For switch node: "
                        + switchId + ", switchComponent: " + switchComponent + ", with Status code: "
                        + oobResponse.getStatusCode() );
                }
                List<Event> switchEvents = oobResponse.getBody();
                logger.debug( "Got Switch Events from HMS-OOB:" + switchEvents );
                return switchEvents;
            }
            catch ( Exception e )
            {
                throw new HmsException( "Error while getting Response from HMS OOB agent. For switch node: " + switchId
                    + ", switchComponent: " + switchComponent, e );
            }
        }
        else
        {
            logger.error( "One of the mandatory inputs while getting events via OOB is NULL. switch node: " + switchId
                + ", switchComponent: " + switchComponent );
            throw new HmsException( "Exception while trying to get On Demand Sensor switch Data." );
        }
    }

    /**
     * Gets Health Monitor Events from OOB Api.
     *
     * @param nodeId
     * @param serverComponent
     * @param targetId
     * @return
     * @throws HmsException
     */
    public static List<Event> getHealthMonitorEventsOOB()
        throws HmsException
    {
        URI uri = null;
        try
        {
            uri = new URI( "http", null, hmsIpAddr, hmsPort, Constants.HMS_OOB_HEALTH_MONITOR_ENDPOINT, null, null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
            HttpEntity<Object> entity = new HttpEntity<Object>( headers );
            ParameterizedTypeReference<List<Event>> typeRef = new ParameterizedTypeReference<List<Event>>()
            {
            };
            ResponseEntity<List<Event>> oobResponse = restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
            if ( oobResponse.getStatusCode() != HttpStatus.OK )
            {
                throw new HmsException( "Error while getting Response from Hms-core. For OOB HEALTH MONITOR AGENT." );
            }
            List<Event> events = oobResponse.getBody();
            logger.error( "Got Events from HMS-OOB: " + Constants.HMS_OOB_HEALTH_MONITOR_ENDPOINT );
            return events;
        }
        catch ( Exception e )
        {
            logger.error( "Error comminicating to HMS - uri" + uri, e );
            return generateHealthMonitorFailureEvents();
        }
    }

    /**
     * In the event of HMS not responding the failure object will be returned for the effective message communication.
     * 
     * @return
     */
    private static List<Event> generateHealthMonitorFailureEvents()
    {
        EventFactory eventFactory = new EventFactory();
        Map<String, String> dataMap = new HashMap<String, String>();
        Map<EventComponent, String> componentsMap = new HashMap<EventComponent, String>();
        String eventText = NodeEvent.HMS_AGENT_DOWN.getEventID().getEventText();
        String value = StringUtils.substringBefore( eventText, HMS_AGENT_STATUS_DESC_SEPARATOR );
        dataMap.put( "unit", EventUnitType.DISCRETE.name() );
        dataMap.put( "eventId", Constants.HMS_OOBAGENT_STATUS );
        dataMap.put( "value", value );
        dataMap.put( "eventName", NodeEvent.HMS_AGENT_DOWN.name() );
        Event event = eventFactory.buildFullEventObject( EventCatalog.HMS_AGENT_DOWN, componentsMap, dataMap,
                                                         Constants.HMS_EVENT_GENERATOR_ID, true );
        List<Event> eventLst = new ArrayList<Event>();
        eventLst.add( event );
        return eventLst;
    }

    /**
     * Gets ServerCOmponent from OOB Api.
     * 
     * @param nodeId
     * @param serverComponent
     * @param targetId
     * @return
     * @throws HmsException
     */
    public static <T> List<T> getServerComponentOOB( String endpoint )
        throws HmsException
    {
        URI uri = null;
        try
        {
            uri = new URI( "http", null, hmsIpAddr, hmsPort, endpoint, null, null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
            HttpEntity<Object> entity = new HttpEntity<Object>( headers );
            ParameterizedTypeReference<List<T>> typeRef = new ParameterizedTypeReference<List<T>>()
            {
            };
            ResponseEntity<List<T>> oobResponse = restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
            if ( oobResponse.getStatusCode() != HttpStatus.OK )
            {
                throw new HmsException( "Error while getting Response from Hms-core for server component. Status Code : "
                    + oobResponse.getStatusCode() );
            }
            List<T> component = oobResponse.getBody();
            logger.info( "Got component from HMS-OOB" + endpoint );
            return component;
        }
        catch ( Exception e )
        {
            throw new HmsException( "Error while getting Response from Hms-core for server component.", e );
        }
    }

    /**
     * Gets ServerCOmponent from OOB Api.
     * 
     * @param nodeId
     * @param serverComponent
     * @param targetId
     * @return
     * @throws HmsException
     */
    public static ServerNode getServerNodeOOB( String endpoint )
        throws HmsException
    {
        URI uri = null;
        try
        {
            uri = new URI( "http", null, hmsIpAddr, hmsPort, endpoint, null, null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
            HttpEntity<Object> entity = new HttpEntity<Object>( headers );
            ResponseEntity<ServerNode> oobResponse =
                restTemplate.exchange( uri, HttpMethod.GET, entity, ServerNode.class );
            if ( oobResponse.getStatusCode() != HttpStatus.OK )
            {
                throw new HmsException( "Error while getting Response from Hms-core for server node. Status Code : "
                    + oobResponse.getStatusCode() );
            }
            ServerNode component = oobResponse.getBody();
            logger.info( "Got node info from HMS-OOB" + endpoint );
            return component;
        }
        catch ( Exception e )
        {
            throw new HmsException( "Error while getting Response from Hms-core for server component.", e );
        }
    }

    /**
     * Gets ServerCOmponent from OOB Api.
     *
     * @param nodeId
     * @param serverComponent
     * @param targetId
     * @return
     * @throws HmsException
     */
    public static SwitchInfo getSwitchNodeOOB( String endpoint )
        throws HmsException
    {
        URI uri = null;
        try
        {
            uri = new URI( "http", null, hmsIpAddr, hmsPort, endpoint, null, null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
            HttpEntity<Object> entity = new HttpEntity<Object>( headers );
            ResponseEntity<SwitchInfo> oobResponse =
                restTemplate.exchange( uri, HttpMethod.GET, entity, SwitchInfo.class );
            if ( oobResponse.getStatusCode() != HttpStatus.OK )
            {
                throw new HmsException( "Error while getting Response from Hms OOB agent for Switch node. Status Code : "
                    + oobResponse.getStatusCode() );
            }
            SwitchInfo component = oobResponse.getBody();
            logger.info( "Got node info from HMS-OOB" + endpoint );
            return component;
        }
        catch ( Exception e )
        {
            throw new HmsException( "Error while getting Response from Hms OOB agent for Switch node.", e );
        }
    }

    /**
     * Gets ServerNode Power Status from OOB Api.
     *
     * @param nodeId
     * @param serverComponent
     * @param targetId
     * @return
     * @throws HmsException
     */
    public static ServerNodePowerStatus getServerNodePowerStatusOOB( String endpoint )
        throws HmsException
    {
        URI uri = null;
        try
        {
            uri = new URI( "http", null, hmsIpAddr, hmsPort, endpoint, null, null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
            HttpEntity<Object> entity = new HttpEntity<Object>( headers );
            ResponseEntity<ServerNodePowerStatus> oobResponse =
                restTemplate.exchange( uri, HttpMethod.GET, entity, ServerNodePowerStatus.class );
            if ( oobResponse.getStatusCode() != HttpStatus.OK )
            {
                throw new HmsException( "Error while getting Response from Hms-core for server node. Status Code : "
                    + oobResponse.getStatusCode() );
            }
            ServerNodePowerStatus status = oobResponse.getBody();
            logger.info( "Got node info from HMS-OOB" + endpoint );
            return status;
        }
        catch ( Exception e )
        {
            throw new HmsException( "Error while getting Response from Hms-core for server power status.", e );
        }
    }

    /**
     * Gets ServerEndpoint Response from OOB Api.
     * 
     * @param nodeId
     * @param serverComponent
     * @param targetId
     * @return
     * @throws HmsException
     */
    public static AboutResponse getServerEndpointAboutResponseOOB( String endpoint )
        throws HmsException
    {
        URI uri = null;
        try
        {
            uri = new URI( "http", null, hmsIpAddr, hmsPort, endpoint, null, null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
            HttpEntity<Object> entity = new HttpEntity<Object>( headers );
            ResponseEntity<AboutResponse> oobResponse =
                restTemplate.exchange( uri, HttpMethod.GET, entity, AboutResponse.class );
            if ( oobResponse.getStatusCode() != HttpStatus.OK )
            {
                throw new HmsException( "Error while getting Response from Hms-core for server endpoint. " + endpoint
                    + ". Status Code : " + oobResponse.getStatusCode() );
            }
            logger.info( "Got component from HMS-OOB : " + endpoint );
            return oobResponse.getBody();
        }
        catch ( Exception e )
        {
            throw new HmsException( "Error while getting Response from Hms-core for server endpoint. " + endpoint, e );
        }
    }

    public static boolean isHMSOOBAvailable()
    {
        URI uri = null;
        try
        {
            uri = new URI( "http", null, hmsIpAddr, hmsPort, Constants.HMS_OOB_ABOUT_ENDPOINT, null, null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", MediaType.APPLICATION_JSON.toString() );
            HttpEntity<Object> entity = new HttpEntity<Object>( headers );
            ParameterizedTypeReference<AboutResponse> typeRef = new ParameterizedTypeReference<AboutResponse>()
            {
            };
            ResponseEntity<AboutResponse> oobResponse = restTemplate.exchange( uri, HttpMethod.GET, entity, typeRef );
            if ( oobResponse.getStatusCode() == HttpStatus.OK )
                return true;
            else
                return false;
        }
        catch ( Exception e )
        {
            logger.debug( "Error while getting Response from Hms OOB Agent.", e );
            return false;
        }
    }

    /**
     * Subscribe to Hms-core for non maskable events during hms-local bootup. To be notified at specified url in
     * hms-local
     */
    public static boolean registerWithHmsCore()
    {
        // If monitoring is NOT enabled, Don't with HMS--core
        if ( !isMonitoringEnabled() )
        {
            logger.warn( "Monitoring Disabled. Not Registering with HMS-core." );
            return false;
        }
        // ResponseEntity<Object> nodes = null;
        URI uri = null;
        try
        {
            uri = new URI( "http", null, hmsIpAddr, hmsPort, Constants.HMS_REGISTER_NME_URI, null, null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", ContentType.APPLICATION_JSON.toString() );
            BaseEventMonitoringSubscription hmsLocalSubscription = new BaseEventMonitoringSubscription();
            hmsLocalSubscription.setRequestMethod( com.vmware.vrack.hms.common.RequestMethod.POST );
            hmsLocalSubscription.setNotificationEndpoint( hmsLocalProtocol + "://" + getHmsLocalIP() + ":"
                + hmsLocalPort + "/" + hmsLocalContext + Constants.HMS_LOCAL_MONITORED_EVENTS_URI );
            List<BaseEventMonitoringSubscription> subscribers = new ArrayList<BaseEventMonitoringSubscription>();
            subscribers.add( hmsLocalSubscription );
            HttpEntity<Object> entity = new HttpEntity<Object>( subscribers, headers );
            restTemplate.exchange( uri, HttpMethod.POST, entity, Object.class );
            return true;
        }
        catch ( HttpStatusCodeException e )
        {
            logger.error( "Unable to Register HMS-Local with HMS-Core for Non-maskable events. Failed Http request for Url:"
                + uri, e );
            return false;
        }
        catch ( URISyntaxException e )
        {
            logger.error( "Unable to Register HMS-Local with HMS-Core for Non-maskable events. Failed to connect to Url:"
                + uri, e );
            return false;
        }
        catch ( Exception e )
        {
            logger.error( "Unable to Register HMS-Local with HMS-Core for Non-maskable events. Url:" + uri, e );
            return false;
        }
    }
}
