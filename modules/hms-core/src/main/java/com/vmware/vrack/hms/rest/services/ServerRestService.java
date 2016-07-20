/* ********************************************************************************
 * ServerRestService.java
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.Form;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsResourceBusyException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.resource.sel.SelOption;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.rest.forms.HMSRestServerActionsForm;
import com.vmware.vrack.hms.task.IHmsTask;
import com.vmware.vrack.hms.task.TaskFactory;
import com.vmware.vrack.hms.task.TaskType;

@Path( "/host" )
public class ServerRestService
{
    private ServerNodeConnector serverConnector = ServerNodeConnector.getInstance();

    private Logger logger = Logger.getLogger( ServerRestService.class );

    private boolean useServerInfoCache =
        Boolean.parseBoolean( HmsConfigHolder.getProperty( HmsConfigHolder.HMS_CONFIG_PROPS, "cache_server_info" ) );

    @GET
    @Path( "/" )
    @Produces( "application/json" )
    public Map<String, HmsNode> getHosts()
        throws HMSRestException
    {
        return serverConnector.nodeMap;
    }

    @GET
    @Path( "/{host_id}" )
    @Produces( "application/json" )
    public ServerNode getHostNode( @PathParam( "host_id" ) String host_id)
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
                executeTask( node, TaskType.PowerStatusServer );
                executeTask( node, TaskType.ServerBoardInfo );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                logger.error( "Error getting node power & discoverable state" );
            }
            return node;
        }
    }

    @GET
    @Path( "/jnlpRemoteConsoleSupportFiles/{host_id}/{path : .*}" )
    @Produces( MediaType.APPLICATION_OCTET_STREAM )
    public Response getJnlpSupportFiles( @PathParam( "host_id" ) String host_id, @PathParam( "path" ) String path)
        throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            try
            {
                ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
                final URL url = new URL( "http://" + node.getManagementIp() + "/" + path );
                StreamingOutput stream = new StreamingOutput()
                {
                    @Override
                    public void write( OutputStream output )
                        throws IOException, WebApplicationException
                    {
                        InputStream input = new BufferedInputStream( url.openStream() );
                        byte[] bufffer = new byte[1024];
                        int read = 0;
                        while ( -1 != ( read = input.read( bufffer ) ) )
                        {
                            output.write( bufffer, 0, read );
                            output.flush();
                        }
                        output.close();
                    }
                };
                ResponseBuilder response = Response.ok( stream );
                response.type( "application/java-archive" );
                return Response.ok( stream ).build();
            }
            catch ( Exception e )
            {
                return Response.serverError().build();
            }
        }
    }

    @GET
    @Path( "/{host_id}/powerstatus" )
    @Produces( "application/json" )
    public ServerNodePowerStatus getHostPowerStatus( @PathParam( "host_id" ) String host_id)
        throws HMSRestException
    {
        ServerNodePowerStatus status = new ServerNodePowerStatus();
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            try
            {
                executeTask( node, TaskType.PowerStatusServer );
                status.setDiscoverable( node.isDiscoverable() );
                status.setPowered( node.isPowered() );
                status.setOperationalStatus( node.getOperationalStatus() );
                return status;
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                logger.error( "Error getting node power & discoverable state : " + host_id );
            }
            return status;
        }
    }

    @GET
    @Path( "/{host_id}/selftest" )
    @Produces( "application/json" )
    public SelfTestResults getHostSelfTestResults( @PathParam( "host_id" ) String host_id)
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
                executeTask( node, TaskType.SelfTest );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            return node.getSelfTestResults();
        }
    }

    @GET
    @Path( "/{host_id}/bmcusers" )
    @Produces( "application/json" )
    public List<BmcUser> getBmcUsers( @PathParam( "host_id" ) String host_id)
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
                executeTask( node, TaskType.ListBmcUsers );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            catch ( Exception e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getBmcUserList();
        }
    }

    @GET
    @Path( "/{host_id}/cpuinfo" )
    @Produces( "application/json" )
    public List<CPUInfo> getCpuInfo( @PathParam( "host_id" ) String host_id)
        throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            if ( node.getCpuInfo() != null && node.getCpuInfo().size() > 0 && useServerInfoCache )
                return node.getCpuInfo();
            try
            {
                executeTask( node, TaskType.RmmCPUInfo );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                if ( e instanceof HMSRestException )
                {
                    throw e;
                }
                else
                {
                    throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                                e.getMessage() );
                }
            }
            catch ( Exception e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getCpuInfo();
        }
    }

    @GET
    @Path( "/{host_id}/storageinfo" )
    @Produces( "application/json" )
    public List<HddInfo> getHddInfo( @PathParam( "host_id" ) String host_id)
        throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            if ( node.getHddInfo() != null && node.getHddInfo().size() > 0 && useServerInfoCache )
                return node.getHddInfo();
            try
            {
                executeTask( node, TaskType.HDDInfo );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                if ( e instanceof HMSRestException )
                {
                    throw e;
                }
                else
                {
                    throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                                e.getMessage() );
                }
            }
            catch ( Exception e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getHddInfo();
        }
    }

    @GET
    @Path( "/{host_id}/storagecontrollerinfo" )
    @Produces( "application/json" )
    public List<StorageControllerInfo> getStorageControllerInfo( @PathParam( "host_id" ) String host_id)
        throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            if ( node.getStorageControllerInfo() != null && node.getStorageControllerInfo().size() > 0
                && useServerInfoCache )
                return node.getStorageControllerInfo();
            try
            {
                executeTask( node, TaskType.StorageControllerInfo );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                if ( e instanceof HMSRestException )
                {
                    logger.error( "Error while getting Storage Controller Info for Node: " + host_id + e );
                    throw e;
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
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getStorageControllerInfo();
        }
    }

    @GET
    @Path( "/{host_id}/memoryinfo" )
    @Produces( "application/json" )
    public List<PhysicalMemory> getMemoryInfo( @PathParam( "host_id" ) String host_id)
        throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            if ( node.getPhysicalMemoryInfo() != null && useServerInfoCache )
                return node.getPhysicalMemoryInfo();
            try
            {
                executeTask( node, TaskType.RmmDimmInfo );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                if ( e instanceof HMSRestException )
                {
                    throw e;
                }
                else
                {
                    throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                                e.getMessage() );
                }
            }
            catch ( Exception e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getPhysicalMemoryInfo();
        }
    }

    @GET
    @Path( "/{host_id}/acpipowerstate" )
    @Produces( "application/json" )
    public AcpiPowerState getHostAcpiPowerState( @PathParam( "host_id" ) String host_id)
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
                executeTask( node, TaskType.AcpiPowerState );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                if ( e instanceof HMSRestException )
                {
                    throw e;
                }
                else
                {
                    throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                                e.getMessage() );
                }
            }
            catch ( Exception e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getAcpiPowerState();
        }
    }

    @GET
    @Path( "/{host_id}/nicinfo" )
    @Produces( "application/json" )
    public List<EthernetController> getNicInfo( @PathParam( "host_id" ) String host_id)
        throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            if ( node.getEthernetControllerList() != null && node.getEthernetControllerList().size() > 0
                && useServerInfoCache )
                return node.getEthernetControllerList();
            try
            {
                executeTask( node, TaskType.NicInfo );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                if ( e instanceof HMSRestException )
                {
                    throw e;
                }
                else
                {
                    throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                                e.getMessage() );
                }
            }
            catch ( Exception e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getEthernetControllerList();
        }
    }

    @PUT
    @Path( "/update" )
    @Consumes( MediaType.APPLICATION_FORM_URLENCODED )
    @Produces( "application/json" )
    public BaseResponse updateNodes( @Form HMSRestServerActionsForm actions )
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        if ( !serverConnector.nodeMap.containsKey( actions.getId() ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + actions.getId() );
        TaskType type = null;
        switch ( actions.getAction() )
        {
            case "cold_reset":
                type = TaskType.ColdResetBmc;
                break;
            case "hard_reset":
                type = TaskType.PowerResetServer;
                break;
            case "power_down":
                type = TaskType.PowerDownServer;
                break;
            case "power_cycle":
                type = TaskType.PowerCycleServer;
                break;
            case "power_up":
                type = TaskType.PowerUpServer;
                break;
        }
        if ( type != null )
        {
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( actions.getId() );
            try
            {
                executeTask( node, type );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
        }
        else
        {
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request", "Can't find action :"
                + actions.getAction() + " to perform on host " + actions.getId() );
        }
        response.setStatusCode( Status.ACCEPTED.getStatusCode() );
        response.setStatusMessage( "Requested update triggered successfully for node: " + actions.getId() );
        return response;
    }

    @PUT
    @Path( "/{host_id}" )
    @Produces( "application/json" )
    public BaseResponse updateNodes( @PathParam( "host_id" ) String host_id, @QueryParam( "action" ) String action)
        throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        try
        {
            NodeAdminStatus nodeAdminAction = NodeAdminStatus.valueOf( action );
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            node.setAdminStatus( nodeAdminAction );
            BaseResponse response = new BaseResponse();
            response.setStatusCode( Status.ACCEPTED.getStatusCode() );
            response.setStatusMessage( nodeAdminAction.getMessage( node.getNodeID() ) );
            return response;
        }
        catch ( IllegalArgumentException e )
        {
            logger.info( "Action not of type NodeOperationalStatus" );
        }
        HMSRestServerActionsForm hmsActionsForm = new HMSRestServerActionsForm();
        hmsActionsForm.setAction( action );
        hmsActionsForm.setId( host_id );
        return updateNodes( hmsActionsForm );
    }

    @GET
    @Path( "/{host_id}/bootoptions" )
    @Produces( "application/json" )
    public SystemBootOptions getSystemBootOptions( @PathParam( "host_id" ) String host_id)
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
                executeTask( node, TaskType.GetSystemBootOptions );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                if ( e instanceof HMSRestException )
                {
                    throw e;
                }
                else
                {
                    throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                                e.getMessage() );
                }
            }
            catch ( Exception e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getSytemBootOptions();
        }
    }

    @PUT
    @Path( "/{host_id}/bootoptions" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( "application/json" )
    public BaseResponse getSystemBootOptions( @PathParam( "host_id" ) String host_id,
                                              SystemBootOptions systemBootOptions)
                                                  throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            BaseResponse response = new BaseResponse();
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            try
            {
                executeTask( node, TaskType.SetSystemBootOptions, systemBootOptions );
                response.setStatusCode( Status.ACCEPTED.getStatusCode() );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
                response.setErrorMessage( "Error while Setting System Boot Options." );
                if ( e instanceof HMSRestException )
                {
                    throw e;
                }
                else
                {
                    throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                                e.getMessage() );
                }
            }
            catch ( Exception e )
            {
                response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
                response.setErrorMessage( "Error while Setting System Boot Options." );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            response.setStatusMessage( "Setting System Boot Options Succeeded." );
            return response;
        }
    }

    @PUT
    @Path( "/{host_id}/chassisidentify" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( "application/json" )
    public BaseResponse chassisIdentify( @PathParam( "host_id" ) String host_id,
                                         ChassisIdentifyOptions chassisIdentifyOptions)
                                             throws HMSRestException
    {
        if ( !serverConnector.nodeMap.containsKey( host_id ) )
            throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                        "Can't find host with id " + host_id );
        else
        {
            BaseResponse response = new BaseResponse();
            ServerNode node = (ServerNode) serverConnector.nodeMap.get( host_id );
            try
            {
                executeTask( node, TaskType.ChassisIdentify, chassisIdentifyOptions );
                response.setStatusCode( Status.ACCEPTED.getStatusCode() );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
                response.setErrorMessage( "Error while sending Chassis Identify Command." );
                if ( e instanceof HMSRestException )
                {
                    throw e;
                }
                else
                {
                    throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                                e.getMessage() );
                }
            }
            catch ( Exception e )
            {
                response.setStatusCode( Status.INTERNAL_SERVER_ERROR.getStatusCode() );
                response.setErrorMessage( "Error while sending Chassis Identify Command." );
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            response.setStatusMessage( "Chassis Identify Succeeded." );
            return response;
        }
    }

    @PUT
    @Path( "/{host_id}/selinfo" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( "application/json" )
    public SelInfo selInfo( @PathParam( "host_id" ) String host_id, SelOption selOption)
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
                executeTask( node, TaskType.SelInfo, selOption );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( HmsException e )
            {
                if ( e instanceof HMSRestException )
                {
                    throw e;
                }
                else
                {
                    throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                                e.getMessage() );
                }
            }
            catch ( Exception e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getSelInfo();
        }
    }

    @GET
    @Path( "/{host_id}/supportedAPI" )
    @Produces( "application/json" )
    public List<HmsApi> getAvailableNodeOperations( @PathParam( "host_id" ) String host_id)
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
                executeTask( node, TaskType.GetSupportedAPI );
            }
            catch ( HmsResourceBusyException e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), Constants.RESOURCE_BUSY,
                                            e.getMessage() );
            }
            catch ( Exception e )
            {
                throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                            e.getMessage() );
            }
            return node.getSupportedHMSAPI();
        }
    }

    private void executeTask( ServerNode node, TaskType taskType )
        throws HMSRestException, HmsResourceBusyException
    {
        try
        {
            TaskResponse taskData = new TaskResponse( node, taskType.toString() );
            IHmsTask task = TaskFactory.getTask( taskType, taskData );
            task.executeTask();
        }
        catch ( HmsResourceBusyException e )
        {
            logger.error( "Encountered HmsResourceBusyException during execution of task", e );
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

    // Some Ipmi commands need to send extra Command parameters in form of byte array.
    // SetSystemBootOptions is an example that needs the command parameters.
    private void executeTask( ServerNode node, TaskType taskType, Object data )
        throws HMSRestException, HmsResourceBusyException
    {
        try
        {
            TaskResponse taskData = new TaskResponse( node, taskType.toString() );
            IHmsTask task = TaskFactory.getTask( taskType, taskData, data );
            task.executeTask();
        }
        catch ( HmsResourceBusyException e )
        {
            logger.error( "Encountered HmsResourceBusyException during execution of task", e );
            throw e;
        }
        catch ( HmsException e )
        {
            logger.error( "Encountered HmsException during execution of task", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
        catch ( Exception e )
        {
            logger.error( "Encountered exception during execution of task", e );
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error", e.getMessage() );
        }
    }
}
