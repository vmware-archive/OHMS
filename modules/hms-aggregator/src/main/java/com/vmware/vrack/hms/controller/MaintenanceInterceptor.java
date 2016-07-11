/* ********************************************************************************
 * MaintenanceInterceptor.java
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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.vmware.vrack.hms.aggregator.ServiceManager;
import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.service.ServiceState;
import com.vmware.vrack.hms.inventory.InventoryLoader;

/**
 * <code>MaintenanceInterceptor</code><br>
 * <p>
 * Intercepts all requests and checks if server host is available for accepting requests.
 *
 * @author VMware, Inc.
 */
public class MaintenanceInterceptor
    extends HandlerInterceptorAdapter
{
    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( MaintenanceInterceptor.class );

    /** The active request counter. */
    private static AtomicInteger activeRequestCounter = new AtomicInteger();

    /**
     * @param request
     * @param response
     * @param handler
     * @return @see {@link HandlerInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)}
     * @throws Exception
     * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#preHandle(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler )
        throws Exception
    {
        String method = request.getMethod();
        String requestURL = request.getRequestURL().toString();
        logger.info( "Received request: [ {} on {} ].", method, requestURL );
        ServiceState serviceState = ServiceManager.getServiceState();
        if ( serviceState.equals( ServiceState.NORMAL_MAINTENANCE )
            || serviceState.equals( ServiceState.FORCE_MAINTENANCE ) )
        {
            // allow upgrade monitoring api, even if service is in maintenance
            // Allow get/set HMS statue even if service is in maintenance
            if ( ( !( method.equalsIgnoreCase( RequestMethod.GET.toString() )
                && requestURL.matches( ".*/upgrade/monitor/.*" ) ) ) && !( requestURL.matches( ".*/state.*" ) ) )
            {
                logger.info( "HMS Service is in {} state. Aborting the request: [ {} on {} ].", serviceState, method,
                             requestURL );
                throw new HMSRestException( Status.SERVICE_UNAVAILABLE.getStatusCode(),
                                            Status.SERVICE_UNAVAILABLE.toString(), " Service is under maintenance." );
            }
        }
        return validateNodeAvialability( request, response );
    }

    /**
     * Validates if node is operational, based on node.isOperational and the host_id passed in request path. All updates
     * of type NodeOperationalStatus are allowed to pass through.
     *
     * @param request the request
     * @param response the response
     * @return true, if successful
     * @throws HMSRestException the HMS rest exception
     */
    private boolean validateNodeAvialability( HttpServletRequest request, HttpServletResponse response )
        throws HMSRestException
    {
        String actionParameter = request.getParameter( "action" );
        if ( actionParameter != null )
        {
            try
            {
                NodeAdminStatus.valueOf( actionParameter );
                activeRequestCounter.incrementAndGet();
                return true;
            }
            catch ( IllegalArgumentException e )
            {
                logger.info( "Action not of type NodeOperationalStatus" );
            }
        }
        String path = (String) request.getAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE );
        String bestMatchPattern = (String) request.getAttribute( HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE );
        AntPathMatcher apm = new AntPathMatcher();
        Map<String, String> mappedRequestParam = apm.extractUriTemplateVariables( bestMatchPattern, path );
        if ( mappedRequestParam.containsKey( "host_id" ) )
        {
            String host_id = mappedRequestParam.get( "host_id" );
            HmsNode host;
            if ( !InventoryLoader.getInstance().getNodeMap().containsKey( host_id ) )
            {
                throw new HMSRestException( Status.NOT_FOUND.getStatusCode(), "Invalid Request",
                                            "Can't find host with id " + host_id );
            }
            else
            {
                host = InventoryLoader.getInstance().getNodeMap().get( host_id );
            }
            if ( host.isNodeOperational() )
            {
                activeRequestCounter.incrementAndGet();
                return true;
            }
            else
            {
                throw new HMSRestException( Status.SERVICE_UNAVAILABLE.getStatusCode(),
                                            Status.SERVICE_UNAVAILABLE.toString(),
                                            "Node " + host_id + " is under maintenance." );
            }
        }
        activeRequestCounter.incrementAndGet();
        return true;
    }

    /**
     * This implementation is empty.
     *
     * @param request the request
     * @param response the response
     * @param handler the handler
     * @param ex the ex
     * @throws Exception the exception
     */
    @Override
    public void afterCompletion( HttpServletRequest request, HttpServletResponse response, Object handler,
                                 Exception ex )
                                     throws Exception
    {
        logger.info( "Returning response code {} for request: [ {} on {} ]", response.getStatus(), request.getMethod(),
                     request.getRequestURI() );
        activeRequestCounter.decrementAndGet();
    }

    /**
     * Gets the active requests.
     *
     * @return the active requests
     */
    public static int getActiveRequests()
    {
        return activeRequestCounter.get();
    }
}
