/* ********************************************************************************
 * HMSManagementRestService.java
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.notification.NodeDiscoveryResponse;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.common.util.ThreadStackLogger;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.node.switches.SwitchNodeConnector;
import com.vmware.vrack.hms.utils.NodeDiscoveryUtil;

@Path( "/" )
public class HMSManagementRestService
{
    public static final String GRANT_EXECUTE_RIGHTS = "chmod +x %s";

    private static Logger logger = Logger.getLogger( HMSManagementRestService.class );

    private ServerNodeConnector serverConnector = ServerNodeConnector.getInstance();

    private SwitchNodeConnector switchConnector = SwitchNodeConnector.getInstance();

    @GET
    @Path( "/nodes" )
    @Produces( "application/json" )
    public Map<String, Object[]> getHMSNodes()
        throws HMSRestException
    {
        Map<String, Object[]> nodes = new HashMap<String, Object[]>();
        try
        {
            Object[] hosts = serverConnector.nodeMap.values().toArray();
            Object[] switches = switchConnector.switchNodeMap.values().toArray();
            nodes.put( Constants.HOSTS, hosts );
            nodes.put( Constants.SWITCHES, switches );
        }
        catch ( Exception e )
        {
            throw new HMSRestException( Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Server Error",
                                        "Error while fetching HMS nodes" );
        }
        return nodes;
    }

    /**
     * Returns node discovery status for nodes present in inventory. discoveryStatus will be "RUNNING", if any of the
     * node's discovery is in "RUNNING" status. If none of the nodes are in "RUNNING" status, it means discovery for all
     * nodes has been completed, and discoveryStatus in the response will be "SUCCESS".
     *
     * @return
     * @throws HMSRestException
     */
    @GET
    @Path( "/discover" )
    @Produces( "application/json" )
    public NodeDiscoveryResponse discoverNodes()
        throws HMSRestException
    {
        return NodeDiscoveryUtil.getNodeDiscoveryStatus();
    }

    @POST
    @Path( "/handshake" )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( "application/json" )
    public BaseResponse handshake()
        throws HMSRestException
    {
        BaseResponse response = new BaseResponse();
        response.setStatusCode( Status.ACCEPTED.getStatusCode() );
        response.setStatusMessage( "Handshake request accepted." );
        return response;
    }

    /**
     * OOB End-Point to return map of node id and the HMS API supported by corresponding OOB Board Plugin
     *
     * @return
     * @throws HMSRestException
     */
    @GET
    @Path( "/service/operations" )
    @Produces( "application/json" )
    public Map<String, List<HmsApi>> getSupportedOOBOperations()
        throws HMSRestException
    {
        return BoardServiceProvider.getOperationSupported();
    }

    /**
     * Return debug information on all HMS threads including stack trace and other useful information.
     */
    @GET
    @Path( "/debug/threads" )
    @Produces( "application/json" )
    public Map<String, String> executeThreadStackLogger()
        throws HMSRestException
    {
        ThreadStackLogger threadStackLogger = new ThreadStackLogger( logger );
        Map<String, String> map = new HashMap<String, String>();
        map.put( "timestamp", Long.toString( System.currentTimeMillis() ) );
        map.put( "threads", threadStackLogger.log() );
        return map;
    }
}
