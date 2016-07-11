/* ********************************************************************************
 * HMSLocalServerRestService.java
 *
 * Copyright © 2013 - 2016 VMware, Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, without warranties or
 * conditions of any kind, EITHER EXPRESS OR IMPLIED. see the License for the
 * specific language governing permissions and limitations under the License
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.aggregator.ServerComponentAggregator;
import com.vmware.vrack.hms.aggregator.util.ServerInfoHelperUtil;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.IInbandService;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.rest.model.CpuInfo;
import com.vmware.vrack.hms.common.rest.model.EthernetController;
import com.vmware.vrack.hms.common.rest.model.FruComponent;
import com.vmware.vrack.hms.common.rest.model.MemoryInfo;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.StorageController;
import com.vmware.vrack.hms.common.rest.model.StorageInfo;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.inventory.FruDataChangeMessage;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

@Controller
@RequestMapping( "/host" )
public class HMSLocalServerRestService
{
    private static Logger logger = Logger.getLogger( HMSLocalServerRestService.class );

    @Autowired
    private HostDataAggregator aggregator;

    @Autowired
    private ApplicationContext context;

    @Value( "${hms.switch.host}" )
    private String hmsIpAddr;

    @Value( "${hms.switch.port}" )
    private int hmsPort;

    @RequestMapping( value = { "/{host_id}/selftest", "/{host_id}/bmcusers", "/{host_id}/acpipowerstate",
        "/{host_id}/bootoptions", "/{host_id}/remoteconsoledetails", "/{host_id}/startremoteconsole" }, method = RequestMethod.GET )
    @ResponseBody
    public ResponseEntity<Object> getMirrorHostInfo( @RequestBody String body, HttpMethod method,
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
                                        "Exception while connecting to hms." + ( ( uri != null ) ? uri.toString() : "" ) );
        }
        return nodes;
    }

    @RequestMapping( value = { "", "/" }, method = RequestMethod.GET )
    @ResponseBody
    public Map<String, ServerNode> getAllServerNodes( @RequestBody String body, HttpMethod method,
                                                      HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        try
        {
            return InventoryLoader.getInstance().getNodeMap();
        }
        catch ( Exception e )
        {
            logger.error( "Error while trying to get hosts: " + e );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while getting serverNodes." );
        }
    }

    @RequestMapping( value = { "/jnlpRemoteConsoleSupportFiles/{host_id}/**" }, method = RequestMethod.GET )
    @ResponseBody
    public void getRemoteConsoleJnlpSupportFiles( HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        URI uri = null;
        try
        {
            uri =
                new URI( "http", null, hmsIpAddr, hmsPort, request.getServletPath() + request.getPathInfo(),
                         request.getQueryString(), null );
            // RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add( "Content-Type", "application/java-archive" );
            InputStream input = new BufferedInputStream( uri.toURL().openStream() );
            StreamUtils.copy( input, response.getOutputStream() );
        }
        catch ( Exception e )
        {
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while connecting to hms." + ( ( uri != null ) ? uri.toString() : "" ) );
        }
    }

    @RequestMapping( value = "/{host_id}", method = RequestMethod.GET )
    @ResponseBody
    public ServerInfo getHostInfo( @PathVariable String host_id )
        throws HMSRestException
    {
        try
        {
            if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
                throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                            "Can't find host with id " + host_id );
            else
            {
                return aggregator.getServerInfo( host_id );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting Server Info for node ID : " + host_id + e );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while getting host infor for node ID : " + host_id );
        }
    }

    @RequestMapping( value = "/{host_id}/powerstatus", method = RequestMethod.GET )
    @ResponseBody
    public ServerNodePowerStatus getHostPowerStatus( @PathVariable String host_id )
        throws HMSRestException
    {
        try
        {
            if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
                throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                            "Can't find host with id " + host_id );
            else
            {
                HostDataAggregator aggregator = new HostDataAggregator();
                return aggregator.getServerNodePowerStatus( host_id );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Exception while getting Server Info for node ID : " + host_id + e );
            throw new HMSRestException( HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error",
                                        "Exception while getting host infor for node ID : " + host_id );
        }
    }

    @RequestMapping( value = "/{host_id}/cpuinfo", method = RequestMethod.GET )
    public @ResponseBody List<CpuInfo> getCpuInfo( @PathVariable String host_id )
        throws HMSRestException
    {
        ServerInfoHelperUtil serverInfoHelperUtil = new ServerInfoHelperUtil();
        List<CpuInfo> listCpuInfo = new ArrayList<CpuInfo>();
        try
        {
            if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
                throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                            "Can't find host with id " + host_id );
            else
            {
                ServerNode node = InventoryLoader.getInstance().getNodeMap().get( host_id );
                ServerComponentAggregator aggregator = new ServerComponentAggregator();
                aggregator.setServerComponentInfo( node, ServerComponent.CPU );
                listCpuInfo = serverInfoHelperUtil.convertToFruCpuInfo( node );
                context.publishEvent( new FruDataChangeMessage( (List<FruComponent>) (List<?>) listCpuInfo, host_id,
                                                                ServerComponent.CPU ) );
                return listCpuInfo;
            }
        }
        catch ( HmsException e )
        {
            if ( e instanceof HMSRestException )
            {
                logger.error( "Error while getting cpuinfo for Node: " + host_id + e );
                throw (HMSRestException) e;
            }
            else
            {
                logger.error( "Error while getting cpuinfo for Node: " + host_id + e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting cpuinfo for Node: " + host_id + e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
    }

    @RequestMapping( value = "/{host_id}/storageinfo", method = RequestMethod.GET )
    public @ResponseBody List<StorageInfo> getHddInfo( @PathVariable String host_id )
        throws HMSRestException
    {
        ServerInfoHelperUtil serverInfoHelperUtil = new ServerInfoHelperUtil();
        List<StorageInfo> listStorageInfo = new ArrayList<StorageInfo>();
        try
        {
            if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
                throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                            "Can't find host with id " + host_id );
            else
            {
                ServerNode node = InventoryLoader.getInstance().getNodeMap().get( host_id );
                ServerComponentAggregator aggregator = new ServerComponentAggregator();
                aggregator.setServerComponentInfo( node, ServerComponent.STORAGE );
                listStorageInfo = serverInfoHelperUtil.convertFruStorageInfo( node );
                context.publishEvent( new FruDataChangeMessage( (List<FruComponent>) (List<?>) listStorageInfo,
                                                                host_id, ServerComponent.STORAGE ) );
                return listStorageInfo;
            }
        }
        catch ( HmsException e )
        {
            if ( e instanceof HMSRestException )
            {
                logger.error( "Error while getting hddinfo for Node: " + host_id + e );
                throw (HMSRestException) e;
            }
            else
            {
                logger.error( "Error while getting hddinfo for Node: " + host_id + e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting hddinfo for Node: " + host_id + e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
    }

    @RequestMapping( value = "/{host_id}/storagecontrollerinfo", method = RequestMethod.GET )
    public @ResponseBody List<StorageController> getStorageControllerInfo( @PathVariable String host_id )
        throws HMSRestException
    {
        ServerInfoHelperUtil serverInfoHelperUtil = new ServerInfoHelperUtil();
        List<StorageController> listStorageController = new ArrayList<StorageController>();
        try
        {
            if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
                throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                            "Can't find host with id " + host_id );
            else
            {
                ServerNode node = InventoryLoader.getInstance().getNodeMap().get( host_id );
                ServerComponentAggregator aggregator = new ServerComponentAggregator();
                aggregator.setServerComponentInfo( node, ServerComponent.STORAGE_CONTROLLER );
                listStorageController = serverInfoHelperUtil.convertToFruStorageControllerInfo( node );
                context.publishEvent( new FruDataChangeMessage( (List<FruComponent>) (List<?>) listStorageController,
                                                                host_id, ServerComponent.STORAGE_CONTROLLER ) );
                return listStorageController;
            }
        }
        catch ( HmsException e )
        {
            if ( e instanceof HMSRestException )
            {
                logger.error( "Error while getting Storage Controller Info for Node: " + host_id + e );
                throw (HMSRestException) e;
            }
            else
            {
                logger.error( "Error while getting Storage Controller Info for Node: " + host_id + e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting Storage Controller Info for Node: " + host_id + e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
    }

    @RequestMapping( value = "/{host_id}/memoryinfo", method = RequestMethod.GET )
    public @ResponseBody List<MemoryInfo> getMemoryInfo( @PathVariable String host_id )
        throws HMSRestException
    {
        ServerInfoHelperUtil serverInfoHelperUtil = new ServerInfoHelperUtil();
        List<MemoryInfo> listMemoryInfo = new ArrayList<MemoryInfo>();
        try
        {
            if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
                throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                            "Can't find host with id " + host_id );
            else
            {
                ServerNode node = InventoryLoader.getInstance().getNodeMap().get( host_id );
                ServerComponentAggregator aggregator = new ServerComponentAggregator();
                aggregator.setServerComponentInfo( node, ServerComponent.MEMORY );
                listMemoryInfo = serverInfoHelperUtil.convertToFruMemoryInfo( node );
                context.publishEvent( new FruDataChangeMessage( (List<FruComponent>) (List<?>) listMemoryInfo, host_id,
                                                                ServerComponent.MEMORY ) );
                return listMemoryInfo;
            }
        }
        catch ( HmsException e )
        {
            if ( e instanceof HMSRestException )
            {
                logger.error( "Error while getting memoryinfo for Node: " + host_id + e );
                throw (HMSRestException) e;
            }
            else
            {
                logger.error( "Error while getting memoryinfo for Node: " + host_id + e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting memoryinfo for Node: " + host_id + e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
    }

    @RequestMapping( value = "/{host_id}/nicinfo", method = RequestMethod.GET )
    public @ResponseBody List<EthernetController> getNicInfo( @PathVariable String host_id )
        throws HMSRestException
    {
        ServerInfoHelperUtil serverInfoHelperUtil = new ServerInfoHelperUtil();
        List<EthernetController> listEthernetController = new ArrayList<EthernetController>();
        try
        {
            if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
                throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                            "Can't find host with id " + host_id );
            else
            {
                ServerNode node = InventoryLoader.getInstance().getNodeMap().get( host_id );
                ServerComponentAggregator aggregator = new ServerComponentAggregator();
                aggregator.setServerComponentInfo( node, ServerComponent.NIC );
                listEthernetController = serverInfoHelperUtil.convertToFruNICInfo( node );
                context.publishEvent( new FruDataChangeMessage( (List<FruComponent>) (List<?>) listEthernetController,
                                                                host_id, ServerComponent.NIC ) );
                return listEthernetController;
            }
        }
        catch ( HmsException e )
        {
            if ( e instanceof HMSRestException )
            {
                logger.error( "Error while getting nicinfo for Node: " + host_id + e );
                throw (HMSRestException) e;
            }
            else
            {
                logger.error( "Error while getting nicinfo for Node: " + host_id + e );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting nicinfo for Node: " + host_id + e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
    }

    @RequestMapping( value = { "/{host_id}/bootoptions", "/{host_id}/chassisidentify", "/{host_id}/selinfo" }, method = RequestMethod.PUT )
    @ResponseBody
    public ResponseEntity<Object> putMirrorHostInfo( @RequestBody String body, HttpMethod method,
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

    @RequestMapping( value = { "/{host_id}" }, method = RequestMethod.PUT )
    public @ResponseBody ResponseEntity<Object> updateNode( @PathVariable String host_id,
                                                            @RequestParam( "action" ) String action,
                                                            @RequestBody String body, HttpMethod method,
                                                            HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException, URISyntaxException, JsonParseException, JsonMappingException, IOException
    {
        try
        {
            if ( InventoryLoader.getInstance().getNode( host_id ) != null )
            {
                NodeAdminStatus nodeAdminAction = NodeAdminStatus.valueOf( action );
                ServerNode node = InventoryLoader.getInstance().getNodeMap().get( host_id );
                node.setAdminStatus( nodeAdminAction );
                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setStatusMessage( String.format( "Admin status for node %s successfully changed to %s.",
                                                              host_id, nodeAdminAction ) );
                baseResponse.setStatusCode( HttpStatus.OK.value() );
                return new ResponseEntity<Object>( baseResponse, HttpStatus.OK );
            }
            else
                throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                            "Can't find host with id " + host_id );
        }
        catch ( IllegalArgumentException e )
        {
            logger.info( "Action not of type NodeOperationalStatus" );
        }
        return putMirrorHostInfo( body, method, request, response );
    }

    private IInbandService getInBandService( ServerNode node )
        throws HmsException
    {
        IInbandService service = InBandServiceProvider.getBoardService( node.getServiceObject() );
        return service;
    }
}
