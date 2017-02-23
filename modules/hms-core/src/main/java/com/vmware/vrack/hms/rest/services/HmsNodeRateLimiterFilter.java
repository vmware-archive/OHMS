/* ********************************************************************************
 * HmsNodeRateLimiterFilter.java
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

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.common.util.CommonProperties;
import com.vmware.vrack.hms.node.NodeMetaInfoProvider;

/**
 * Filter used to limit the concurrent operations running on a node
 */
@Provider
public class HmsNodeRateLimiterFilter
    implements ContainerRequestFilter, ContainerResponseFilter
{

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger( HmsNodeRateLimiterFilter.class );

    /**
     * Thread local variable used to determine if the count of concurrent operations on a node needs to be decremented
     * if it was incremented by the same request thread in nodeMetaInfoMap of NodeMetaInfoProvider class
     **/
    private static final ThreadLocal<String> threadLocalNodeIdentifier = new ThreadLocal<String>();

    @Override
    public void filter( ContainerRequestContext reqCtx, ContainerResponseContext rspCtx )
        throws IOException
    {

        logger.debug( "Returning response code {} for request: {} {}", rspCtx.getStatus(), reqCtx.getMethod(),
                      reqCtx.getUriInfo().getAbsolutePath() );

        if ( threadLocalNodeIdentifier.get() != null )
        {
            String nodeId = threadLocalNodeIdentifier.get();

            // We will try to attempt decreasing operationCount for node atleast
            // three times before giving up.
            int maxRetries = 3;
            int retries = 0;

            for ( ; retries < maxRetries; ++retries )
            {
                boolean releasedLock = NodeMetaInfoProvider.decreaseConcurrentOperationCount( nodeId );
                if ( !releasedLock )
                {
                    logger.warn( "Failed while attempting to release the lock for Node: {}. Attempt # {}", nodeId,
                                 retries );

                    try
                    {
                        Thread.sleep( CommonProperties.getConcurrentOperationRetryThreadSleepTime() );
                    }
                    catch ( InterruptedException e )
                    {
                        logger.warn( "Thread interrupted during releasing lock for node {} " + nodeId, e );
                    }
                }
                else
                {
                    logger.info( "Successfully released the lock for Node: {}. After Attempt # {}", nodeId, retries );
                    break;
                }
            }

            if ( retries >= maxRetries )
            {
                logger.error( "Reached max retries to while attempting to release the lock for Node: {}. "
                    + "Attempt # {}. System might cause some issue.", nodeId, retries );
            }

            threadLocalNodeIdentifier.remove();
        }
    }

    @Override
    public void filter( ContainerRequestContext reqCtx )
        throws IOException
    {

        String method = reqCtx.getMethod();
        String requestURI = reqCtx.getUriInfo().getAbsolutePath().toString();

        logger.debug( "Received request: [ {} on {} ].", method, requestURI );

        if ( reqCtx instanceof PostMatchContainerRequestContext )
        {
            processRequest( (PostMatchContainerRequestContext) reqCtx );
        }
    }

    /**
     * Filter to check if the given node is already running given number of concurrent operations on the service and the
     * requests can be executed.
     *
     * @param reqCtx
     */
    private void processRequest( PostMatchContainerRequestContext reqCtx )
    {

        String requestURI = reqCtx.getUriInfo().getAbsolutePath().toString();
        String nodeId = null;

        if ( requestURI.matches( ".*/host/.*" ) )
        {

            if ( requestURI.matches( ".*/update.*" ) || requestURI.matches( ".*/event/host/HMS" ) )
            {
                // Simply forward the request if it is for updating the
                // powerStatus or getting the HMS events
                return;
            }
            else if ( requestURI.matches( ".*/event/.*" ) && requestURI.matches( ".*/nme/.*" ) )
            {
                // extracts the nodeId from URL of
                // type(/api/1.0/hms/event/host/nme/{host_id}/)
                nodeId = extractNodeIdFromEventURI( requestURI );
            }
            else
            {
                // extracts the nodeId from URL of
                // type(/api/1.0/hms/event/host/N4/CPU and
                // /api/1.0/hms/host/N4/powerstatus)
                nodeId = extractNodeIdFromURI( requestURI, EventComponent.SERVER );
            }

        }
        else if ( requestURI.matches( ".*/switches/.*" ) )
        {

            if ( requestURI.matches( ".*/event/.*" ) && requestURI.matches( ".*/nme/.*" ) )
            {
                // extracts the nodeId from URL of
                // type(/api/1.0/hms/event/switches/nme/{switch_id}/)
                nodeId = extractNodeIdFromEventURI( requestURI );
            }
            else
            {
                nodeId = extractNodeIdFromURI( requestURI, EventComponent.SWITCH );
            }
        }
        else
        {

            return;
        }

        if ( nodeId != null && StringUtils.isNotBlank( nodeId ) )
        {
            nodeId = nodeId.trim();

            while ( !NodeMetaInfoProvider.increaseConcurrentOperationCount( nodeId ) )
            {
                try
                {
                    Thread.sleep( CommonProperties.getConcurrentOperationRetryThreadSleepTime() );
                }
                catch ( InterruptedException e )
                {
                    logger.warn( "Thread interrupted during releasing lock for node {}", nodeId, e );
                }
            }
            threadLocalNodeIdentifier.set( nodeId );
        }
    }

    /**
     * Gives nodeId from requestURI of pattern /hms/event/host/nme/{host_id}/
     *
     * @param requestURI
     * @return
     */
    public String extractNodeIdFromEventURI( String requestURI )
    {
        if ( requestURI != null )
        {
            String subStringContainingNodeId = StringUtils.substringAfter( requestURI, "/nme/" );
            return extractNodeId( subStringContainingNodeId );
        }

        return null;
    }

    /**
     * Returns Node Id from a given Request URI(of type - api/1.0/hms/host/N4/powerstatus) for switch or host
     *
     * @param requestURI
     * @return
     */
    public String extractNodeIdFromURI( String requestURI, EventComponent component )
    {

        if ( requestURI != null && component != null )
        {
            String subStringContainingNodeId = null;

            if ( EventComponent.SWITCH.equals( component ) )
            {
                subStringContainingNodeId = StringUtils.substringAfter( requestURI, "/switches/" );
            }
            else if ( EventComponent.SERVER.equals( component ) )
            {
                subStringContainingNodeId = StringUtils.substringAfter( requestURI, "/host/" );
            }
            else
            {
                logger.debug( "Component not supported while extracting nodeId from Request URI: {} ", requestURI );
                return null;
            }

            if ( subStringContainingNodeId != null && subStringContainingNodeId.length() != 0 )
            {
                return extractNodeId( subStringContainingNodeId );
            }
        }

        return null;
    }

    /**
     * Finds out the nodeId given only part of URL of type(N4/CPU/) which starts with NodeId
     *
     * @param uri
     * @return
     */
    public String extractNodeId( String uri )
    {
        if ( uri != null )
        {
            if ( uri.contains( "/" ) )
            {
                String nodeId = StringUtils.substringBefore( uri, "/" );
                return nodeId.trim();
            }
            else
            {
                return uri.trim();
            }
        }
        else
        {
            return null;
        }
    }

}
