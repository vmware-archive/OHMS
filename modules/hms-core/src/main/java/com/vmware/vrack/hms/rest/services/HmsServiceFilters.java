/* ********************************************************************************
 * HmsServiceFilters.java
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
package com.vmware.vrack.hms.rest.services;

import java.io.IOException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.hms.ServiceManager;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.service.ServiceState;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;

/**
 * The Class HmsServiceFilters.
 */
@Provider
public class HmsServiceFilters
    implements ContainerRequestFilter, ContainerResponseFilter
{
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( HmsServiceFilters.class );

    @Override
    public void filter( ContainerRequestContext reqCtx, ContainerResponseContext rspCtx )
        throws IOException
    {
        logger.debug( "Returning response code {} for request: {} {}", rspCtx.getStatus(), reqCtx.getMethod(),
                      reqCtx.getUriInfo().getAbsolutePath() );
    }

    @Override
    public void filter( ContainerRequestContext reqCtx )
        throws IOException
    {
        String method = reqCtx.getMethod();
        String requestURI = reqCtx.getUriInfo().getAbsolutePath().toString();
        logger.debug( "Received request: [ {} on {} ].", method, requestURI );
        // if service is in MAINTENANCE, abort request and send a response of
        // 503 (Service Unavailable)
        if ( ServiceManager.getServiceState().equals( ServiceState.NORMAL_MAINTENANCE )
            || ServiceManager.getServiceState().equals( ServiceState.FORCE_MAINTENANCE ) )
        {
            // allow hms upgrade monitoring api, even if service in maintenance
            if ( !( method.equalsIgnoreCase( HttpMethod.GET ) && requestURI.matches( ".*/upgrade/monitor/.*" ) ) )
            {
                logger.debug( "Service is in MAINTENANCE. Abort request: {} {}", reqCtx.getMethod(),
                              reqCtx.getUriInfo().getAbsolutePath() );
                BaseResponse baseResponse =
                    new BaseResponse( Status.SERVICE_UNAVAILABLE.getStatusCode(), "Service Unavialble",
                                      "Service is in MAINTENANCE state." );
                reqCtx.abortWith( Response.status( Status.SERVICE_UNAVAILABLE.getStatusCode() ).entity( baseResponse ).type( MediaType.APPLICATION_JSON ).build() );
            }
        }
        if ( reqCtx instanceof PostMatchContainerRequestContext )
        {
            isNodeOperational( (PostMatchContainerRequestContext) reqCtx );
        }
    }

    /**
     * Filter to check if node is operational and the requests can be executed.
     *
     * @param reqCtx
     */
    private void isNodeOperational( PostMatchContainerRequestContext reqCtx )
    {
        UriInfo info = reqCtx.getUriInfo();
        String methodName = null;
        String className = null;
        ResourceMethodInvoker method = reqCtx.getResourceMethod();
        if ( method != null )
        {
            methodName = method.getMethod().getName();
            className = method.getMethod().getDeclaringClass().getCanonicalName();
        }
        if ( methodName.equalsIgnoreCase( "updateNodes" )
            && className.equalsIgnoreCase( "com.vmware.vrack.hms.rest.services.ServerRestService" ) )
        {
            MultivaluedMap<String, String> queryParameters = info.getQueryParameters();
            if ( queryParameters != null && queryParameters.containsKey( "action" ) )
            {
                try
                {
                    NodeAdminStatus.valueOf( queryParameters.get( "action" ).get( 0 ) );
                    return;
                }
                catch ( IllegalArgumentException e )
                {
                    logger.info( "Action not of type NodeOperationalStatus" );
                }
            }
        }
        MultivaluedMap<String, String> parameters = info.getPathParameters();
        if ( parameters != null && parameters.containsKey( "host_id" ) )
        {
            HmsNode node = ServerNodeConnector.getInstance().nodeMap.get( parameters.get( "host_id" ).get( 0 ) );
            if ( node != null && !node.isNodeOperational() )
            {
                reqCtx.abortWith( Response.status( Status.SERVICE_UNAVAILABLE.getStatusCode() ).entity( new BaseResponse( Status.SERVICE_UNAVAILABLE.getStatusCode(),
                                                                                                                          "Service Unavialble",
                                                                                                                          node.getAdminStatus().getMessage( node.getNodeID() ) ) ).type( MediaType.APPLICATION_JSON ).build() );
                logger.debug( "Abort request: {} {} ", reqCtx.getMethod(), reqCtx.getUriInfo().getAbsolutePath() );
            }
        }
    }
}
