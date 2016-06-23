/* ********************************************************************************
 * HMSLocalRestService.java
 *
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vmware.vrack.common.event.Event;
import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.aggregator.EventGeneratorTask;
import com.vmware.vrack.hms.aggregator.HMSAboutResponseAggregator;
import com.vmware.vrack.hms.aggregator.TopologyAggregator;
import com.vmware.vrack.hms.aggregator.util.InventoryUtil;
import com.vmware.vrack.hms.boardservice.ib.InbandServiceImpl;
import com.vmware.vrack.hms.common.events.EventMonitoringSubscriptionHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.notification.NodeDiscoveryResponse;
import com.vmware.vrack.hms.common.resource.AboutResponse;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.SwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.topology.NetTopElement;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.inventory.HmsCacheValidateEnum;
import com.vmware.vrack.hms.inventory.HmsDataCache;
import com.vmware.vrack.hms.inventory.InventoryLoader;

/**
 * Generic Class to handle requests coming to hms-local.
 */
/**
 * <code>HMSLocalRestService</code><br>
 *
 * @author VMware, Inc.
 */
@Controller
public class HMSLocalRestService
{
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( HMSLocalRestService.class );

    /** The inband service impl. */
    InbandServiceImpl inbandServiceImpl = new InbandServiceImpl();

    /** The hms data cache. */
    @Autowired
    private HmsDataCache hmsDataCache;

    /** The hms ip addr. */
    @Value( "${hms.switch.host}" )
    private String hmsIpAddr;

    /** The hms port. */
    @Value( "${hms.switch.port}" )
    private int hmsPort;

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

    /**
     * Gets the mirror host info.
     *
     * @param body the body
     * @param method the method
     * @param request the request
     * @param response the response
     * @return the mirror host info
     * @throws HMSRestException the HMS rest exception
     * @throws URISyntaxException the URI syntax exception
     * @throws JsonParseException the json parse exception
     * @throws JsonMappingException the json mapping exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @RequestMapping( value = { "/handshake", "/inventory", "/switches", "/switches/{switch_id}/**" } )
    @ResponseBody
    public ResponseEntity<Object> getMirrorHostInfo( @RequestBody String body, HttpMethod method,
                                                     HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException, URISyntaxException, JsonParseException, JsonMappingException, IOException
    {
        ResponseEntity<Object> nodes;
        URI uri = null;
        try
        {
            uri =
                new URI( "http", null, hmsIpAddr, hmsPort, request.getServletPath() + request.getPathInfo(),
                         request.getQueryString(), null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", request.getContentType() );
            HttpEntity<Object> entity = new HttpEntity<Object>( body, headers );
            nodes = restTemplate.exchange( uri, HttpMethod.valueOf( request.getMethod() ), entity, Object.class );
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
                                        "Exception while connecting to hms." + ( ( uri != null ) ? uri.toString() : "" ) );
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
    public SwitchInfo getSwitchInfo( @RequestBody String body, HttpMethod method, HttpServletRequest request,
                                     HttpServletResponse response )
        throws HMSRestException
    {
        ResponseEntity<SwitchInfo> switchInfoResponse;
        URI uri = null;
        try
        {
            uri =
                new URI( "http", null, hmsIpAddr, hmsPort, request.getServletPath() + request.getPathInfo(),
                         request.getQueryString(), null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", request.getContentType() );
            HttpEntity<Object> entity = new HttpEntity<Object>( body, headers );
            switchInfoResponse =
                restTemplate.exchange( uri, HttpMethod.valueOf( request.getMethod() ), entity, SwitchInfo.class );
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
                                        "Exception while getting the switchInfo."
                                            + ( ( uri != null ) ? uri.toString() : "" ) );
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
    public ResponseEntity<Object> updateSwitchNode( @RequestBody String body, HttpMethod method,
                                                    HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        ResponseEntity<Object> nodes;
        URI uri = null;
        try
        {
            uri =
                new URI( "http", null, hmsIpAddr, hmsPort, request.getServletPath() + request.getPathInfo(),
                         request.getQueryString(), null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", request.getContentType() );
            HttpEntity<Object> entity = new HttpEntity<Object>( body, headers );
            nodes = restTemplate.exchange( uri, HttpMethod.valueOf( request.getMethod() ), entity, Object.class );
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
                                        "Exception while updating switch Info."
                                            + ( ( uri != null ) ? uri.toString() : "" ) );
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
    public ResponseEntity<Object> getAllNodes( @RequestBody String body, HttpMethod method, HttpServletRequest request,
                                               HttpServletResponse response )
        throws HMSRestException
    {
        ResponseEntity<Object> nodes = null;
        URI uri = null;
        try
        {
            ResponseEntity<HashMap<String, Object[]>> oobResponse =
                InventoryUtil.getInventoryOOB( hmsIpAddr, hmsPort, hmsOobPathInfo, "application/json" );
            Map<String, Object[]> respBody = oobResponse.getBody();
            respBody.put( Constants.HOSTS, InventoryLoader.getInstance().getNodeMap().values().toArray() );
            nodes = new ResponseEntity<Object>( respBody, oobResponse.getHeaders(), oobResponse.getStatusCode() );
            logger.debug( "Available Inband NodeMap : {}", InventoryLoader.getInstance().getNodeMap() );
        }
        catch ( HttpStatusCodeException e )
        {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType( MediaType.APPLICATION_JSON );
            nodes = new ResponseEntity<Object>( e.getResponseBodyAsString(), headers, e.getStatusCode() );
            logger.error( "Http Status Code Exception when trying to get hms/nodes.", e );
        }
        catch ( Exception e )
        {
            logger.error( "Error while trying to get nodes.", e );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms." + ( ( uri != null ) ? uri.toString() : "" ) );
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
    public Map<String, ServerInfo> getAllHostCache( @RequestBody String body, HttpMethod method,
                                                    HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        try
        {
            ResponseEntity<HashMap<String, Object[]>> oobResponse =
                InventoryUtil.getInventoryOOB( hmsIpAddr, hmsPort, hmsOobPathInfo, "application/json" );
            Map<String, Object[]> respBody = oobResponse.getBody();
            respBody.put( Constants.HOSTS, InventoryLoader.getInstance().getNodeMap().values().toArray() );
            Object[] hosts = respBody.get( "hosts" );
            HmsCacheValidateEnum validate = hmsDataCache.validateHostCache( hosts );
            if ( validate != null && validate.equals( HmsCacheValidateEnum.VALID ) )
            {
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
    public Map<String, NBSwitchInfo> getAllSwitchCache( @RequestBody String body, HttpMethod method,
                                                        HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        try
        {
            ResponseEntity<HashMap<String, Object[]>> oobResponse =
                InventoryUtil.getInventoryOOB( hmsIpAddr, hmsPort, hmsOobPathInfo, "application/json" );
            Map<String, Object[]> respBody = oobResponse.getBody();
            Object[] switches = respBody.get( Constants.SWITCHES );
            HmsCacheValidateEnum validate = hmsDataCache.validateSwitchCache( switches );
            if ( validate != null && validate.equals( HmsCacheValidateEnum.VALID ) )
            {
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
    public NodeDiscoveryResponse getDiscoveryStatus( @RequestBody String body, HttpMethod method,
                                                     HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        ResponseEntity<NodeDiscoveryResponse> discoveryStatusResponse;
        URI uri = null;
        try
        {
            uri =
                new URI( "http", null, hmsIpAddr, hmsPort, request.getServletPath() + request.getPathInfo(),
                         request.getQueryString(), null );
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", request.getContentType() );
            HttpEntity<Object> entity = new HttpEntity<Object>( body, headers );
            discoveryStatusResponse =
                restTemplate.exchange( uri, HttpMethod.valueOf( request.getMethod() ), entity,
                                       NodeDiscoveryResponse.class );
            return discoveryStatusResponse.getBody();
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms." + ( ( uri != null ) ? uri.toString() : "" ) );
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
    public List<NetTopElement> getNetTop( @RequestBody String body, HttpMethod method, HttpServletRequest request,
                                          HttpServletResponse response )
        throws HMSRestException
    {
        return topologyAggregator.getNetworkTopology( body, method, request, response );
    }

    /**
     * Initializes Hms Inventory on HMS Bootup and constantly seeks inventory details at defined interval.
     */
    @PostConstruct
    public void initializeInventoryOnHmsIbBootup()
    {
        // Constantly try to get inventory file from Hms OOB and write in
        // inventory file .
        // Required because during first time, when HMS Local boots up, there us
        // no guarantee that HMS OOB is up and responsive.
        // This will make sure to fetch the inventory from Hms OOB and write it
        // in inventory file as soon Hms OOB comes up.
        ExecutorService fetchInventoryService = Executors.newFixedThreadPool( 1 );
        fetchInventoryService.execute( new Runnable()
        {
            @Override
            public void run()
            {
                do
                {
                    try
                    {
                        initializeInventory();
                    }
                    catch ( Exception e )
                    {
                        logger.warn( "Still waiting to initialize memory", e );
                    }
                    finally
                    {
                        try
                        {
                            Thread.sleep( hmsOobInventoryPollInterval );
                        }
                        catch ( InterruptedException e )
                        {
                            // Ignore exception here.
                        }
                    }
                }
                while ( !InventoryUtil.isFileExists( hmsIbInventoryLocation ) );
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
            InventoryUtil.initializeInventory( hmsIbInventoryLocation, hmsIpAddr, hmsPort, hmsOobPathInfo );
            response.setStatusCode( Status.OK.getStatusCode() );
            response.setStatusMessage( "Hms inventory refreshed successfully." );
            InventoryLoader.getInstance().setNodeComponentSupportedOob( InventoryUtil.getOOBSupportedOperations( hmsIpAddr,
                                                                                                                 hmsPort,
                                                                                                                 Constants.HMS_OOB_SUPPORTED_OPEARIONS_ENDPOINT,
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
     * @throws HMSRestException the HMS rest exception
     */
    @RequestMapping( value = "/refreshinventory", method = RequestMethod.POST )
    @ResponseBody
    public BaseResponse refreshInventory( @RequestBody String body, HttpMethod method )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        if ( hmsIbInventoryLocation != null )
        {
            initializeInventory();
            response.setStatusCode( Status.OK.getStatusCode() );
            response.setStatusMessage( "Hms inventory refreshed successFully." );
            return response;
        }
        else
        {
            String err = "Cannot refresh Inventory as the  inventory location is: " + hmsIbInventoryLocation;
            logger.error( err );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", err );
        }
    }
}
