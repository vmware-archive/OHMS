/* ********************************************************************************
 * HMSLocalRestService.java
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
package com.vmware.vrack.hms.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.aggregator.EventGeneratorTask;
import com.vmware.vrack.hms.aggregator.HMSAboutResponseAggregator;
import com.vmware.vrack.hms.aggregator.HmsOutOfBandHandshakeTask;
import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.aggregator.TopologyAggregator;
import com.vmware.vrack.hms.aggregator.util.InventoryUtil;
import com.vmware.vrack.hms.aggregator.util.ServerInfoHelperUtil;
import com.vmware.vrack.hms.boardservice.ib.InbandServiceImpl;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.notification.NodeDiscoveryResponse;
import com.vmware.vrack.hms.common.resource.AboutResponse;
import com.vmware.vrack.hms.common.rest.model.DhcpLease;
import com.vmware.vrack.hms.common.rest.model.PhysicalRackInfo;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.switches.adapters.SwitchInfoAssemblers;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.topology.NetTopElement;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.HmsGenericUtil;
import com.vmware.vrack.hms.inventory.HmsCacheValidateEnum;
import com.vmware.vrack.hms.inventory.HmsDataCache;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.inventory.ServerDataChangeMessage;
import com.vmware.vrack.hms.inventory.SwitchDataChangeMessage;
import com.vmware.vrack.hms.rest.factory.HmsOobAgentRestTemplate;

/**
* Generic Class to handle requests coming to hms-aggregator.
*/
/**
 * <code>HMSLocalRestService</code><br>
 *
 * @author VMware, Inc.
 */
@SuppressWarnings( "deprecation" )
@Controller
@DependsOn( value = { "commonProperties", "URIBuilder", "springContextHelper" } )
public class HMSLocalRestService
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( HMSLocalRestService.class );

    /** The inband service impl. */
    InbandServiceImpl inbandServiceImpl = new InbandServiceImpl();

    /** The hms data cache. */
    @Autowired
    private HmsDataCache hmsDataCache;

    @Autowired
    private ApplicationContext context;

    /** The hms oob path info. */
    @Value( "${hms.oob.nodes.pathinfo}" )
    private String hmsOobPathInfo;

    /** The hms ib inventory location. */
    @Value( "${hms.ib.inventory.location}" )
    private String hmsIbInventoryLocation;

    /** The hms oob inventory poll interval. */
    @Value( "${hms.oob.inventory.poll.interval}" )
    private int hmsOobInventoryPollInterval;

    /** The hms oob username. */
    @Value( "${hms.switch.username}" )
    private String hmsOobUsername;

    /** The hms oob password. */
    @Value( "${hms.switch.password}" )
    private String hmsOobPassword;

    /** The topology aggregator. */
    @Autowired
    TopologyAggregator topologyAggregator;

    @Autowired
    HmsOutOfBandHandshakeTask outOfBandHandShakeTask;

    @Autowired
    private HostDataAggregator hostDataAggregator;

    /**
     * Gets the mirror host info.
     *
     * @param body the body
     * @param method the method
     * @param request the request
     * @param response the response
     * @return the mirror host info
     * @throws HMSRestException the HMS rest exception
     * @throws JsonParseException the json parse exception
     * @throws JsonMappingException the json mapping exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping( value = { "/inventory", "/switches", "/switches/{switch_id}/**" } )
    @ResponseBody
    public ResponseEntity<Object> getMirrorHostInfo( @RequestBody( required = false ) String body, HttpMethod method,
                                                     HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException, JsonParseException, JsonMappingException, IOException
    {
        ResponseEntity<Object> nodes;
        String path = null;

        try
        {
            path = request.getServletPath() + request.getPathInfo();
            String query = request.getQueryString();
            HttpMethod httpMethod = HttpMethod.valueOf( request.getMethod() );
            String contentType = request.getContentType();

            HmsOobAgentRestTemplate<String> restTemplate = new HmsOobAgentRestTemplate<String>( body, contentType );
            nodes = restTemplate.exchange( httpMethod, path, query, Object.class );

        }
        catch ( HttpStatusCodeException e )
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON );
            nodes = new ResponseEntity<Object>( e.getResponseBodyAsString(), headers, e.getStatusCode() );
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms." + ( ( path != null ) ? path : "" ) );
        }
        return nodes;
    }

    /**
     * Gets the switch events.
     *
     * @param switch_id the switch_id
     * @param event_source the event_source
     * @return the switch events
     * @throws HmsException the hms exception
     */
    @RequestMapping( value = "/event/switches/{switch_id}/{event_source}", method = RequestMethod.GET )
    @ResponseBody
    public List<Event> getSwitchEvents( @PathVariable( "switch_id" ) String switch_id,
                                        @PathVariable( "event_source" ) EventComponent event_source )
        throws HmsException
    {

        try
        {
            logger.debug( "trying to get Events for Switch: {} for component: {}", switch_id, event_source );

            SwitchComponentEnum component = EventMonitoringSubscriptionHolder.getMappedSwitchComponents( event_source );
            EventGeneratorTask eventGenerator = new EventGeneratorTask();
            return eventGenerator.getAggregatedSwitchEvents( switch_id, component );

        }
        catch ( HmsException e )
        {
            logger.error( "HMSException. Error getting sensor events for switch :{} component: {}", switch_id,
                          event_source, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
        catch ( Exception e )
        {
            logger.error( "Exception. Error getting sensor events for switch: {} component: {}", switch_id,
                          event_source, e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
    }

    /**
     * Gets the switch info.
     *
     * @param body the body
     * @param method the method
     * @param request the request
     * @param response the response
     * @return the switch info
     * @throws HMSRestException the HMS rest exception
     */
    @RequestMapping( value = "/switches/{switch_id}", method = RequestMethod.GET )
    @ResponseBody
    public SwitchInfo getSwitchInfo( @RequestBody( required = false ) String body, HttpMethod method,
                                     HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        ResponseEntity<SwitchInfo> switchInfoResponse;
        String path = null;
        try
        {
            path = request.getServletPath() + request.getPathInfo();
            String query = request.getQueryString();
            HttpMethod httpMethod = HttpMethod.valueOf( request.getMethod() );
            String contentType = request.getContentType();

            HmsOobAgentRestTemplate<String> restTemplate = new HmsOobAgentRestTemplate<String>( body, contentType );
            switchInfoResponse = restTemplate.exchange( httpMethod, path, query, SwitchInfo.class );

            if ( switchInfoResponse.getStatusCode() != HttpStatus.OK )
            {
                throw new HmsException( "Error while getting Response from Hms-core for switch Info. Status Code : "
                    + switchInfoResponse.getStatusCode() );
            }
            SwitchInfo switchInfo = switchInfoResponse.getBody();
            return switchInfo;
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while getting the switchInfo." + ( ( path != null ) ? path : "" ) );
        }
    }

    /**
     * Update switch node.
     *
     * @param body the body
     * @param method the method
     * @param request the request
     * @param response the response
     * @return the response entity
     * @throws HMSRestException the HMS rest exception
     */
    @RequestMapping( value = "/switches/{switch_id}", method = RequestMethod.PUT )
    @ResponseBody
    public ResponseEntity<Object> updateSwitchNode( @RequestBody( required = false ) String body, HttpMethod method,
                                                    HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {

        ResponseEntity<Object> nodes;
        String path = null;
        try
        {
            path = request.getServletPath() + request.getPathInfo();
            String query = request.getQueryString();
            HttpMethod httpMethod = HttpMethod.valueOf( request.getMethod() );
            String contentType = request.getContentType();

            HmsOobAgentRestTemplate<String> restTemplate = new HmsOobAgentRestTemplate<String>( body, contentType );
            nodes = restTemplate.exchange( httpMethod, path, query, Object.class );

        }
        catch ( HttpStatusCodeException e )
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON );
            nodes = new ResponseEntity<Object>( e.getResponseBodyAsString(), headers, e.getStatusCode() );
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while updating switch Info." + ( ( path != null ) ? path : "" ) );
        }
        return nodes;
    }

    /**
     * Gets the about response.
     *
     * @return the about response
     * @throws HMSRestException the HMS rest exception
     */
    @RequestMapping( value = "/about", method = RequestMethod.GET )
    @ResponseBody
    public Map<String, AboutResponse> getAboutResponse()
        throws HMSRestException
    {
        try
        {
            HMSAboutResponseAggregator aggregator = new HMSAboutResponseAggregator();
            return aggregator.getHMSAboutResponse();

        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting About Information for HMS.", e );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while getting HMS Build Version" );
        }
    }

    /**
     * Gets the all nodes.
     *
     * @param body the body
     * @param method the method
     * @param request the request
     * @param response the response
     * @return the all nodes
     * @throws HMSRestException the HMS rest exception
     */
    @RequestMapping( value = "/nodes", method = RequestMethod.GET )
    @ResponseBody
    public Map<String, Object[]> getAllNodes( @RequestBody( required = false ) String body, HttpMethod method,
                                              HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {

        Map<String, Object[]> nodes = new HashMap<String, Object[]>();
        try
        {

            Object[] hosts = InventoryLoader.getInstance().getNodeMap().values().toArray();
            Object[] switches = InventoryLoader.getInstance().getSwitchNodeMap().values().toArray();

            List<ServerNode> copyHosts = new ArrayList<ServerNode>();
            List<SwitchNode> copySwitches = new ArrayList<SwitchNode>();

            if ( hosts != null && hosts.length > 0 )
            {
                for ( Object obj : hosts )
                {
                    ServerNode serverNode = (ServerNode) obj;
                    ServerNode copy = (ServerNode) HmsGenericUtil.maskPassword( serverNode );
                    copyHosts.add( copy );
                }
            }

            if ( switches != null && switches.length > 0 )
            {
                for ( Object obj : switches )
                {
                    SwitchNode switchNode = (SwitchNode) obj;
                    SwitchNode copy = (SwitchNode) HmsGenericUtil.maskPassword( switchNode );
                    copySwitches.add( copy );
                }
            }

            nodes.put( Constants.HOSTS, copyHosts.toArray() );
            nodes.put( Constants.SWITCHES, copySwitches.toArray() );

            logger.debug( "Available Inband NodeMap : {}", InventoryLoader.getInstance().getNodeMap() );
        }
        catch ( Exception e )
        {
            logger.error( "Error while trying to get nodes.", e );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while trying to get nodes." );
        }
        return nodes;

    }

    /**
     * Returns the HMS cache Data (ServerInfo).
     *
     * @param body the body
     * @param method the method
     * @param request the request
     * @param response the response
     * @return List of Nodes
     * @throws HMSRestException the HMS rest exception
     */
    @RequestMapping( value = "/host/cache", method = RequestMethod.GET )
    @ResponseBody
    public Map<String, ServerInfo> getAllHostCache( @RequestBody( required = false ) String body, HttpMethod method,
                                                    HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        try
        {
            Collection<ServerNode> hosts = InventoryLoader.getInstance().getNodeMap().values();

            logger.debug( "Got the Host list Inventory to validate the HMS In memory Host cache data" );
            HmsCacheValidateEnum validate = hmsDataCache.validateHostCache( hosts );
            if ( validate != null && validate.equals( HmsCacheValidateEnum.VALID ) )
            {
                logger.debug( "HMS In memory Host cache data validation successful, returing the Host cache" );
                return hmsDataCache.getServerInfoMap();
            }
            else
            {
                throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                            "HMS Server cache is not complete or invalid" );
            }

        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting HMS Hosts cache Information", e );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while getting HMS Hosts cache Information, HMS Server cache is not complete or invalid" );
        }
    }

    /**
     * Returns the HMS cache Data (SwitchInfo).
     *
     * @param body the body
     * @param method the method
     * @param request the request
     * @param response the response
     * @return List of Nodes
     * @throws HMSRestException the HMS rest exception
     */
    @RequestMapping( value = "/switch/cache", method = RequestMethod.GET )
    @ResponseBody
    public Map<String, NBSwitchInfo> getAllSwitchCache( @RequestBody( required = false ) String body, HttpMethod method,
                                                        HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        try
        {
            ResponseEntity<HashMap<String, Object[]>> oobResponse =
                InventoryUtil.getInventoryOOB( hmsOobPathInfo, "application/json" );
            Map<String, Object[]> respBody = oobResponse.getBody();

            Object[] switches = respBody.get( Constants.SWITCHES );

            logger.debug( "Got the Switch list Inventory to validate the HMS In memory Switch cache data" );
            HmsCacheValidateEnum validate = hmsDataCache.validateSwitchCache( switches );
            if ( validate != null && validate.equals( HmsCacheValidateEnum.VALID ) )
            {
                logger.debug( "HMS In memory Switch cache data validation successful, returing the Switch cache" );
                return hmsDataCache.getSwitchInfoMap();
            }
            else
            {
                throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                            "HMS switch cache is not complete or invalid" );
            }

        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting HMS Switchs cache Information", e );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while getting HMS Switchs cache Information, HMS Switch cache is not complete or invalid" );
        }
    }

    /**
     * Gets the discovery status.
     *
     * @param body the body
     * @param method the method
     * @param request the request
     * @param response the response
     * @return the discovery status
     * @throws HMSRestException the HMS rest exception
     */
    @RequestMapping( value = "/discover", method = RequestMethod.GET )
    @ResponseBody
    public NodeDiscoveryResponse getDiscoveryStatus( @RequestBody( required = false ) String body, HttpMethod method,
                                                     HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {

        ResponseEntity<NodeDiscoveryResponse> discoveryStatusResponse;
        String path = request.getServletPath() + request.getPathInfo();

        try
        {
            String query = request.getQueryString();
            HttpMethod httpMethod = HttpMethod.valueOf( request.getMethod() );
            String contentType = request.getContentType();

            HmsOobAgentRestTemplate<String> restTemplate = new HmsOobAgentRestTemplate<String>( body, contentType );
            discoveryStatusResponse = restTemplate.exchange( httpMethod, path, query, NodeDiscoveryResponse.class );
            return discoveryStatusResponse.getBody();
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms." + ( ( path != null ) ? path : "" ) );
        }
    }

    /**
     * Gets the net top.
     *
     * @param body the body
     * @param method the method
     * @param request the request
     * @param response the response
     * @return the net top
     * @throws HMSRestException the HMS rest exception
     */
    @RequestMapping( value = "/topology", method = RequestMethod.GET )
    @ResponseBody
    public List<NetTopElement> getNetTop( @RequestBody( required = false ) String body, HttpMethod method,
                                          HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {

        return topologyAggregator.getNetworkTopology( body, method, request, response );
    }

    /**
     * Performs the below tasks during startup. This will be retried on regular intervals of time 1. Initialize
     * inventory at Out of band <br/>
     * 2. Initialize inventory at Aggregator <br/>
     * 3. build inventory cache <br />
     */
    @PostConstruct
    public void initialize()
    {
        // Constantly try to initialize the inventory and create the Hms memory
        // cache
        ExecutorService fetchInventoryService = Executors.newFixedThreadPool( 1 );
        fetchInventoryService.execute( new Runnable()
        {
            @Override
            public void run()
            {
                boolean isOobInvRefreshed = false;
                boolean isTaskCompleted = false;
                int retryCounter = 1;
                do
                {
                    try
                    {
                        // First make sure that the inventory is loaded at Out
                        // of band and then proceed with the remaining steps
                        if ( !isOobInvRefreshed )
                        {
                            if ( InventoryUtil.refreshInventoryOnOutOfBand() )
                            {
                                isOobInvRefreshed = true;
                            }
                            else
                            {
                                logger.warn( "Trying to initialize inventory on Out of band, will retry in: {}ms, "
                                    + "retry count: {}", hmsOobInventoryPollInterval, retryCounter );
                                continue;
                            }
                        }
                        // Init inventory at aggregator
                        BaseResponse respose = initializeInventory();
                        if ( respose.getStatusCode() == Status.INTERNAL_SERVER_ERROR.getStatusCode() )
                        {
                            logger.error( "initializeInventory failed, will continue retry" );
                            continue;
                        }

                        logger.debug( "initializeInventory is completed" );

                        // Create HMS In memory cache while HMS aggregator
                        // boots up with Inventory loader data.
                        logger.debug( "Start creating the HMS In memory HOST and SWITCH cache "
                            + "while HMS aggregator boots up with Inventory loader data." );

                        // Builds cache
                        createHmsInMemoryCacheOnBootUp();

                        logger.debug( "Successfully created the HMS In memory HOST and SWITCH "
                            + "cache while HMS aggregator boots up with Inventory loader data." );

                        isTaskCompleted = true;
                    }
                    catch ( Throwable e )
                    {
                        logger.error( "Trying to initialize inventory, will retry in: {}ms, retry count: {}, and "
                            + "the exception is: {}", hmsOobInventoryPollInterval, retryCounter, e );
                    }
                    finally
                    {
                        HmsGenericUtil.sleepThread( isTaskCompleted, hmsOobInventoryPollInterval );
                        ++retryCounter;
                    }
                }
                while ( !isTaskCompleted );
            }
        } );

        // Make sure Executor will NOT take any new tasks further and will close
        // itself once tasks are completed
        fetchInventoryService.shutdown();
    }

    /**
     * Initialize inventory at the startup.
     *
     * @return the base response
     */
    public BaseResponse initializeInventory()
    {
        logger.debug( "Initializing inventory" );
        BaseResponse response = new BaseResponse();
        try
        {
            InventoryUtil.initializeInventory( hmsIbInventoryLocation, hmsOobPathInfo );
            response.setStatusCode( Status.OK.getStatusCode() );
            response.setStatusMessage( "Hms inventory refreshed successfully." );
            InventoryLoader.getInstance().setNodeComponentSupportedOob( InventoryUtil.getOOBSupportedOperations( Constants.HMS_OOB_SUPPORTED_OPEARIONS_ENDPOINT,
                                                                                                                 "application/json" ) );
        }
        catch ( Exception e )
        {
            logger.error( "Error while trying to initialize inventory.", e );
            response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
            response.setStatusMessage( "Failed to initialize Hms inventory." );
        }
        return response;
    }

    /**
     * This endpoint will be called indicating that the inventory file has been modified lately. Now at this point, Hms
     * will try to refresh inventory with the upgraded inventory file
     *
     * @param body the body
     * @param method the method
     * @return the base response
     * @throws HmsException
     */
    @RequestMapping( value = "/refreshinventory", method = RequestMethod.POST )
    @ResponseBody
    public ResponseEntity<BaseResponse> refreshInventory( @RequestBody( required = false ) String body,
                                                          HttpMethod method )
        throws HmsException
    {
        if ( hmsIbInventoryLocation != null )
        {

            BaseResponse response = new BaseResponse();

            /*
             * Though it returns a boolean, checking its return value is useless. Because, this can either throw an
             * exception or returns true.
             */
            InventoryUtil.initializeInventory( hmsIbInventoryLocation, hmsOobPathInfo );

            // refresh oob inventory
            if ( InventoryUtil.refreshInventoryOnOutOfBand() )
            {
                logger.info( "In refreshInventory, refreshed OOB inventory." );
            }
            else
            {
                response.setStatusCode( HttpStatus.INTERNAL_SERVER_ERROR.value() );
                response.setErrorMessage( "Failed to refresh OOB inventory." );
                return new ResponseEntity<BaseResponse>( response, HttpStatus.INTERNAL_SERVER_ERROR );
            }

            // update oob supported operations, as we refreshed oob inventory
            Map<String, List<HmsApi>> oobSupportedOperationsMap =
                InventoryUtil.getOOBSupportedOperations( Constants.HMS_OOB_SUPPORTED_OPEARIONS_ENDPOINT,
                                                         "application/json" );
            if ( oobSupportedOperationsMap == null || oobSupportedOperationsMap.isEmpty() )
            {
                logger.error( "In refreshInventory, failed to get oob supported operations, "
                    + "after refreshing oob inventory." );
                response.setStatusCode( HttpStatus.INTERNAL_SERVER_ERROR.value() );
                response.setStatusMessage( "Failed to get oob supported operations, after refreshing oob inventory." );
                return new ResponseEntity<BaseResponse>( response, HttpStatus.INTERNAL_SERVER_ERROR );
            }
            logger.debug( "In refreshInventory, got oob supported operations, after refreshing oob inventory." );
            InventoryLoader.getInstance().setNodeComponentSupportedOob( oobSupportedOperationsMap );

            /*
             * Refresh the HMS cache from refreshed Inventory loader Data before returning.
             */
            updateHmsCacheOnRefreshInventory();
            response.setStatusCode( HttpStatus.OK.value() );
            response.setStatusMessage( "Hms inventory refreshed successFully." );
            return new ResponseEntity<BaseResponse>( response, HttpStatus.OK );
        }
        else
        {
            String err = "Cannot refresh Inventory as the  inventory location is: " + hmsIbInventoryLocation;
            logger.error( err );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", err );
        }
    }

    /**
     * Refresh the HMS cache upon refresh inventory request from the Inventory loader Data.
     */
    public void updateHmsCacheOnRefreshInventory()
    {
        Map<String, ServerNode> inventoryNodeMap = InventoryLoader.getInstance().getNodeMap();
        Map<String, ServerInfo> cacheInfoMap = hmsDataCache.getServerInfoMap();

        // if cache is not initialized, nothing to update it.
        if ( cacheInfoMap == null || cacheInfoMap.isEmpty() )
        {
            logger.warn( "In updateHmsCacheOnRefreshInventory, ServerInfo in Cache is not initialized. "
                + "Not updating cache." );
            return;
        }
        if ( inventoryNodeMap == null || inventoryNodeMap.isEmpty() )
        {
            logger.warn( "In updateHmsCacheOnRefreshInventory, ServerNode Map of Inventory is either null or empty." );
            return;
        }
        for ( String key : inventoryNodeMap.keySet() )
        {
            String nodeId = inventoryNodeMap.get( key ).getNodeID();
            ServerNode serverNode = inventoryNodeMap.get( key );
            try
            {
                ServerInfo serverInfo = cacheInfoMap.get( nodeId );
                if ( serverInfo != null )
                {
                    logger.debug( "Updating the HMS cache for the node: {}", nodeId );
                    ServerInfoHelperUtil.updateServerInfoData( serverInfo, serverNode );
                    context.publishEvent( new ServerDataChangeMessage( serverInfo, ServerComponent.SERVER ) );
                }
                else
                {

                    /*
                     * ServerNode is in inventory, but ServerInfo is not in Cache. Could be a new server commissioned.
                     * Update cache with the ServerInfo for the new Server.
                     */
                    logger.debug( "In updateHmsCacheOnRefreshInventory, host '{}' is in inventory, but not in cache. "
                        + "Updating cache with the ServerInfo for the host.", nodeId );
                    serverInfo = hostDataAggregator.getServerInfo( nodeId );
                    hmsDataCache.updateHmsDataCache( nodeId, ServerComponent.SERVER, serverInfo );
                }
            }
            catch ( Exception e )
            {
                logger.warn( "In updateHmsCacheOnRefreshInventory, error while updating cache for host '{}'.", nodeId,
                             e );
            }
        }
    }

    /**
     * Creates HMS In memory cache (host/cache and switch/cache) with Inventory loader data.
     *
     * @return
     */
    public void createHmsInMemoryCacheOnBootUp()
    {
        Map<String, ServerNode> serverNodeMap = InventoryLoader.getInstance().getNodeMap();
        Map<String, SwitchNode> switchNodeMap = InventoryLoader.getInstance().getSwitchNodeMap();

        if ( serverNodeMap != null && serverNodeMap.size() > 0 )
        {
            logger.info( "Creating HMS In memory HOST cache while HMS aggregator boots up with Inventory loader data." );
            try
            {
                for ( ServerNode serverNode : serverNodeMap.values() )
                {
                    ServerInfo serverInfo = new ServerInfo();
                    serverInfo = ServerInfoHelperUtil.convertToServerInfo( serverNode );
                    context.publishEvent( new ServerDataChangeMessage( serverInfo, ServerComponent.SERVER ) );
                    logger.debug( "Created HMS In memory HOST cache for host {} while HMS aggregator boots up with Inventory loader data.",
                                  serverNode.getNodeID() );
                }
            }
            catch ( Exception e )
            {
                logger.error( "Error while creating HMS In memory HOST cache while HMS aggregator boots up with Inventory loader data: {}",
                              e );
            }
        }
        if ( switchNodeMap != null && switchNodeMap.size() > 0 )
        {
            logger.info( "Creating HMS In memory SWITCH cache while HMS aggregator boots up with Inventory loader data." );
            try
            {
                for ( SwitchNode switchNode : switchNodeMap.values() )
                {
                    NBSwitchInfo nbSwitchInfo = new NBSwitchInfo();
                    nbSwitchInfo = SwitchInfoAssemblers.toNBSwitchInfoFromSwitchNode( switchNode );
                    context.publishEvent( new SwitchDataChangeMessage( nbSwitchInfo, SwitchComponentEnum.SWITCH ) );
                    logger.debug( "Created HMS In memory SWITCH cache for switch {} while HMS aggregator boots up with Inventory loader data.",
                                  switchNode.getSwitchId() );
                }
            }
            catch ( Exception e )
            {
                logger.error( "Error while creating HMS In memory SWITCH cache while HMS aggregator boots up with Inventory loader data: {}",
                              e );
            }
        }
    }

    @RequestMapping( value = { "/host/inventory" }, method = RequestMethod.PUT )
    public @ResponseBody ResponseEntity<Object> updateInventory( @RequestBody( required = false ) String body )
        throws HMSRestException, URISyntaxException, JsonParseException, JsonMappingException, IOException
    {
        List<ServerInfo> requestServerInfoList = null;
        if ( body != null )
        {
            ObjectMapper mapper = new ObjectMapper();
            try
            {
                PhysicalRackInfo physicalRackInfo = mapper.readValue( body, PhysicalRackInfo.class );
                requestServerInfoList = physicalRackInfo.getServerNodeList();
                if ( requestServerInfoList == null )
                {
                    throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), "Invalid Request",
                                                "No server list found to update the inventory " );
                }
            }
            catch ( Exception e )
            {
                String error = "Invalid request body to update the inventory";
                logger.error( error, e );
                throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), error, e.getMessage() );
            }
        }

        Map<String, Object[]> nodes = null;
        Map<String, Object> updatedServerInfoMap = null;

        try
        {
            nodes = InventoryUtil.initializeInventory( hmsIbInventoryLocation );
            List<String> invalidHostsFromRequest =
                InventoryUtil.getInvalidHostsinRequest( nodes.get( "hosts" ), requestServerInfoList );
            updatedServerInfoMap = InventoryUtil.getUpdatedServerInfos( nodes.get( "hosts" ), requestServerInfoList );

            logger.info( String.format( "InvalidHostsFromRequest %s ", invalidHostsFromRequest ) );
            ObjectMapper ibInventoryobjectMapper = new ObjectMapper();
            Object[] serverInfosArray =
                ibInventoryobjectMapper.convertValue( updatedServerInfoMap.values(), new TypeReference<Object[]>()
                {
                } );

            nodes.put( "hosts", serverInfosArray );

            ObjectMapper nodeObjectMapper = new ObjectMapper();
            // Save the updated Inventory data in the File system
            // hms-ib-inventory.json at path hmsIbInventoryLocation
            InventoryUtil.createOrUpdateInventoryFile( hmsIbInventoryLocation,
                                                       nodeObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString( nodes ) );

            // Refresh inventory with the upgraded hms inventory file
            refreshInventory( null, HttpMethod.POST );

            if ( invalidHostsFromRequest.isEmpty() )
            {
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setStatusMessage( "Successfully updated the HMS inventory" );
                baseResponse.setStatusCode( HttpStatus.OK.value() );
                return new ResponseEntity<Object>( baseResponse, HttpStatus.OK );
            }
            else
            {
                String errorMessage = String.format( "Unable to update the HMS inventory, for invalid hosts %s",
                                                     invalidHostsFromRequest );
                logger.error( errorMessage );
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setStatusMessage( errorMessage );
                baseResponse.setStatusCode( HttpStatus.BAD_REQUEST.value() );
                return new ResponseEntity<Object>( baseResponse, HttpStatus.BAD_REQUEST );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Exception occured while updating the HMS inventory", e );
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setStatusMessage( String.format( "Unable to update the HMS inventory, %s",
                                                          e.getCause().getMessage() ) );
            baseResponse.setStatusCode( HttpStatus.INTERNAL_SERVER_ERROR.value() );
            return new ResponseEntity<Object>( baseResponse, HttpStatus.INTERNAL_SERVER_ERROR );
        }

    }

    @RequestMapping( value = { "/handshake" } )
    @ResponseBody
    public ResponseEntity<Object> handshake( @RequestBody( required = false ) String body, HttpMethod method,
                                             HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException, JsonParseException, JsonMappingException, IOException
    {
        logger.debug( "Received request for handshake" );
        if ( InventoryUtil.isInventoryLoaded() )
        {
            logger.debug( "Inventory has been loaded" );
            return getMirrorHostInfo( body, method, request, response );
        }
        else
        {
            logger.debug( "Inventory is not yet loaded, in progress" );
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setStatusMessage( "Inventory is not yet loaded" );
            baseResponse.setStatusCode( HttpStatus.SERVICE_UNAVAILABLE.value() );
            return new ResponseEntity<Object>( baseResponse, HttpStatus.SERVICE_UNAVAILABLE );
        }
    }

    @SuppressWarnings( "unchecked" )
    @RequestMapping( value = { "/newhosts" }, method = RequestMethod.GET )
    @ResponseBody
    public <T> ResponseEntity<T> getNewHosts()
        throws HMSRestException
    {
        logger.info( "In getNewHosts, getting new hosts from OOB Agent." );
        try
        {
            HmsOobAgentRestTemplate<String> restTemplate = new HmsOobAgentRestTemplate<String>();
            ResponseEntity<String> response =
                restTemplate.exchange( HttpMethod.GET, Constants.HMS_OOB_NEW_HOSTS, String.class );
            HttpStatus httpStatus = response.getStatusCode();
            String responseBody = response.getBody();
            if ( httpStatus == HttpStatus.INTERNAL_SERVER_ERROR )
            {
                // oob agent returns BaseResponse.
                BaseResponse baseResponse = HmsGenericUtil.parseStringAsValueType( responseBody, BaseResponse.class );
                if ( baseResponse != null )
                {
                    return (ResponseEntity<T>) new ResponseEntity<BaseResponse>( baseResponse, httpStatus );
                }
                else
                {
                    baseResponse = new BaseResponse();
                    baseResponse.setStatusCode( httpStatus.value() );
                    baseResponse.setStatusMessage( httpStatus.getReasonPhrase() );
                    baseResponse.setErrorMessage( responseBody );
                    return (ResponseEntity<T>) new ResponseEntity<BaseResponse>( baseResponse, httpStatus );
                }
            }
            else if ( httpStatus == HttpStatus.OK )
            {
                TypeReference<List<DhcpLease>> typeRef = new TypeReference<List<DhcpLease>>()
                {
                };
                List<DhcpLease> dhcpLeases = HmsGenericUtil.parseStringAsTypeReference( responseBody, typeRef );
                return (ResponseEntity<T>) new ResponseEntity<List<DhcpLease>>( dhcpLeases, httpStatus );
            }
        }
        catch ( HmsException | RestClientException e )
        {
            logger.error( "In getNewHosts, error getting new hosts from OOB Agent.", e );
            BaseResponse baseResponse =
                HmsGenericUtil.getBaseResponse( HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                "Error while getting new hosts from OOB Agent.", e.getMessage() );
            return (ResponseEntity<T>) new ResponseEntity<BaseResponse>( baseResponse,
                                                                         HttpStatus.INTERNAL_SERVER_ERROR );
        }
        return null;
    }

    @RequestMapping( value = "/handshake/{aggregator_ip}", method = RequestMethod.POST )
    @ResponseBody
    public ResponseEntity<Object> handshake( @PathVariable( "aggregator_ip" ) String aggregatorIp )
        throws HMSRestException, JsonParseException, JsonMappingException, IOException
    {

        logger.debug( "Received request for handshake using aggregator future ip: {}", aggregatorIp );

        if ( StringUtils.isBlank( aggregatorIp ) )
        {
            throw new HMSRestException( Status.BAD_REQUEST.getStatusCode(), "aggregator/vrm future ip can't be blank",
                                        "please provide future aggregator/vrm ip" );
        }
        try
        {
            aggregatorIp = aggregatorIp.trim();
            outOfBandHandShakeTask.init( aggregatorIp, Constants.VRM );
        }
        catch ( HmsException e )
        {
            logger.error( "Exception occured while performing the handshake: {}", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "can't handshake with oob", e );
        }
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatusMessage( "Handshake is successful on VRM Ip" );
        baseResponse.setStatusCode( HttpStatus.OK.value() );

        return new ResponseEntity<Object>( baseResponse, HttpStatus.OK );
    }
}