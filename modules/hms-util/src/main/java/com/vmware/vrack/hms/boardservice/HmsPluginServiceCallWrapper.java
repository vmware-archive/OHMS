/*
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/* ********************************************************************************
 * HmsPluginServiceCallWrapper.java
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
package com.vmware.vrack.hms.boardservice;

import com.vmware.vrack.common.event.enums.EventComponent;
import com.vmware.vrack.hms.common.ExternalService;
import com.vmware.vrack.hms.common.boardvendorservice.api.IHmsComponentService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.exception.HmsResourceBusyException;
import com.vmware.vrack.hms.common.exception.HmsResponseTimeoutException;
import com.vmware.vrack.hms.common.switches.api.ISwitchService;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.CommonProperties;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.String.format;

/**
 * Wrapper class to perform operations on the BoardServices in a rate limited fashion. This class will help us to limit
 * how many parallel operations (Method calls) can run in parallel on any particular Node. This will NOT drop the call
 * requests for operations on the node, but queue it up, in ExecutorService. It will also limit the max amount of time
 * for which any operation can keep running for any operation on the BoardService. Node cane be either Switch OR Server
 *
 * @author Yagnesh Chawda
 */
public class HmsPluginServiceCallWrapper
{
    private static Logger logger = Logger.getLogger( HmsPluginServiceCallWrapper.class );

    private static Integer THREAD_POOL_COUNT = CommonProperties.getPluginThreadPoolCount();

    private static Long PLUGIN_TASK_TIMEOUT = CommonProperties.getPluginTaskTimeOut();

    // Key = Node Id, Value = Object of ScheduledExecutorService and ThreadLimitExecuterServiceObject
    private static Map<String, NodeRateLimitModel> cachedNodeRateLimitObject =
        new ConcurrentHashMap<>();

    public static <T> T invokeHmsPluginService( IHmsComponentService service, ServiceServerNode serviceServerNode,
                                                String methodName, Object[] methodArgs )
        throws HmsException, HmsResourceBusyException,
        HmsResponseTimeoutException
    {
        return invokeHmsPluginService( service, serviceServerNode, methodName, methodArgs, PLUGIN_TASK_TIMEOUT );
    }

    /**
     * Invokes the method being asked for along with the passed arguments using reflection on the given "service" object
     *
     * @param service
     * @param serviceServerNode
     * @param methodName
     * @param methodArgs
     * @param taskTimeOut
     * @param <T>
     * @return
     * @throws HmsException
     * @throws HmsResourceBusyException
     * @throws HmsResponseTimeoutException
     */
    public static <T> T invokeHmsPluginService( IHmsComponentService service, ServiceServerNode serviceServerNode,
                                                String methodName, Object[] methodArgs, long taskTimeOut )
        throws HmsException, HmsResourceBusyException, HmsResponseTimeoutException
    {
        if ( serviceServerNode != null && serviceServerNode.getNodeID() != null
            && !"".equals( serviceServerNode.getNodeID().trim() ) && service != null )
        {
            return submitAndExecuteTask( EventComponent.SERVER, serviceServerNode.getNodeID(), service, taskTimeOut,
                                         methodName, methodArgs );
        }
        else
        {
            String err = format( "Unable to invoke Server service[%s] for server[%s] and method[%s]", service,
                                 serviceServerNode, methodName );
            logger.error( err );
            throw new HmsException( err );
        }
    }

    /**
     * Returns NodeRateLimitModelObject for Node Will create and cache it, if node does not have
     * NodeRateLimitModelObject
     *
     * @param nodeId
     * @return
     */
    public static NodeRateLimitModel getNodeRateLimitModelObject( String nodeId )
    {
        return (NodeRateLimitModel) cachedNodeRateLimitObject.get( nodeId );
    }

    /**
     * Creates a NodeRateLimitModel Object for nodeId and puts it in cachedNodeRateLimitObject Map
     *
     * @param nodeId
     * @return
     */
    public static boolean addNodeRateLimitModelForNode( String nodeId )
    {
        if ( nodeId == null || !nodeId.isEmpty() )
        {
            NodeRateLimitModel nodeRateLimitModel = new NodeRateLimitModel();
            ThreadLimitExecuterServiceObjectPool serviceObjectPool =
                ThreadLimitExecuterServiceObjectPool.getInstance( THREAD_POOL_COUNT );
            ScheduledExecutorService serverExecutor = Executors.newScheduledThreadPool( THREAD_POOL_COUNT );
            nodeRateLimitModel.setScheduledExecutorService( serverExecutor );
            nodeRateLimitModel.setThreadLimitExecuterServiceObject( serviceObjectPool );
            cachedNodeRateLimitObject.put( nodeId, nodeRateLimitModel );
            return true;
        }
        else
        {
            logger.error( "nodeId is null" );
        }
        return false;
    }

    public static <T> T invokeHmsPluginSwitchService( IHmsComponentService service, SwitchNode switchNode,
                                                      String methodName, Object[] methodArgs )
        throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException
    {
        return invokeHmsPluginSwitchService( service, switchNode, PLUGIN_TASK_TIMEOUT, methodName, methodArgs );
    }

    /**
     * Invokes the method being asked for along with the passed arguments using reflection on the given "service" object
     *
     * @param service
     * @param switchNode
     * @param taskTimeOut
     * @param methodName
     * @param methodArgs
     * @param <T>
     * @return
     * @throws HmsException
     * @throws HmsResourceBusyException
     * @throws HmsResponseTimeoutException
     */
    public static <T> T invokeHmsPluginSwitchService( IHmsComponentService service, SwitchNode switchNode,
                                                      long taskTimeOut, String methodName, Object[] methodArgs )
        throws HmsException, HmsResourceBusyException, HmsResponseTimeoutException
    {
        // Get executer service for Switch and submit it, and then wait for its
        // Future object here itself, and return once it is completed.
        if ( switchNode != null && switchNode.getSwitchId() != null && !"".equals( switchNode.getSwitchId().trim() )
            && service instanceof ISwitchService )
        {
            return submitAndExecuteTask( EventComponent.SWITCH, switchNode.getSwitchId(), service, taskTimeOut,
                                         methodName, methodArgs );
        }
        else
        {
            String err = format( "Unable to invoke switch service[%s] for switchNode[%s] and method[%s]",
                                 service, switchNode, methodName );
            logger.error( err );
            throw new HmsException( err );
        }
    }

    public static <T> T invokeHmsPluginExternalServiceService( IHmsComponentService service,
                                                               ExternalService externalService,
                                                               String methodName, Object[] methodArgs )
        throws HmsResourceBusyException, HmsResponseTimeoutException, HmsException
    {
        return invokeHmsPluginExternalServiceService( service, externalService, PLUGIN_TASK_TIMEOUT, methodName,
                                                      methodArgs );
    }

    /**
     * Invokes the method being asked for along with the passed arguments using reflection on the given "service" object
     *
     * @param service
     * @param externalService
     * @param taskTimeOut
     * @param methodName
     * @param methodArgs
     * @param <T>
     * @return
     * @throws HmsException
     * @throws HmsResourceBusyException
     * @throws HmsResponseTimeoutException
     */
    public static <T> T invokeHmsPluginExternalServiceService( IHmsComponentService service,
                                                               ExternalService externalService,
                                                               long taskTimeOut, String methodName,
                                                               Object[] methodArgs )
        throws HmsException, HmsResourceBusyException, HmsResponseTimeoutException
    {
        if ( externalService != null && externalService.getServiceEndpoint() != null
            && !externalService.getServiceEndpoint().trim().isEmpty() )
        {
            /**
             * Get executor service for ExternalService and submit it, and then wait
             * for its Future object here itself, and return once it is completed.
             */
            return submitAndExecuteTask( EventComponent.SERVER, externalService.getServiceEndpoint(),
                                         service, taskTimeOut, methodName, methodArgs );
        }
        else
        {
            String err = format( "Unable to invoke board service[%s] for service[%s] and method[%s]",
                                 service, externalService, methodName );
            logger.error( err );
            throw new HmsException( err );
        }
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private static <T> T submitAndExecuteTask( EventComponent eventComponent, String nodeId,
                                               IHmsComponentService service, long taskTimeOut,
                                               String methodName, Object[] methodArgs )
        throws HmsException, HmsResourceBusyException, HmsResponseTimeoutException
    {
        Callable serviceTask = null;
        Future<Object> taskFuture = null;
        ThreadLimitExecuterServiceObject threadLimitExecuterServiceObject = null;
        NodeRateLimitModel nodeRateLimitModel = getNodeRateLimitModelObject( nodeId );
        try
        {
            threadLimitExecuterServiceObject =
                nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().borrowObject( nodeId );
        }
        catch ( NoSuchElementException e2 )
        {
            throw new HmsResourceBusyException(
                format( "HMS Resource is Busy for node %s. Please try after some time", nodeId ),
                e2 );
        }
        catch ( Exception e )
        {
            String err = format( "Exception while borrowing Object from object pool for Node [%s]", nodeId );
            logger.fatal( err, e );
            throw new HmsException( err, e );
        }
        serviceTask = new HmsCommonInvokerTask( service, methodName, methodArgs );
        taskFuture = nodeRateLimitModel.getScheduledExecutorService().submit( serviceTask );
        try
        {
            return (T) taskFuture.get( taskTimeOut, TimeUnit.MILLISECONDS );
        }
        catch ( TimeoutException e )
        {
            String err = format( "Unable to complete task within given time interval for " + eventComponent
                                     + "-node[%s] and method[%s]", nodeId, methodName );
            taskFuture.cancel( true );
            logger.error( err, e );
            throw new HmsResponseTimeoutException( err, e );
        }
        catch ( ExecutionException e )
        {
            String err = format( "Unable to complete task for " + eventComponent + "-node[%s] and method[%s]",
                                 nodeId, methodName );
            logger.error( err, e );
            Throwable cause = e.getCause();
            if ( cause instanceof HmsException )
            {
                throw (HmsException) cause;
            }
            else
            {
                throw new HmsException( err, e );
            }
        }
        catch ( Exception e )
        {
            String err = format( "Unable to complete task for " + eventComponent + "-node[%s] and method[%s]",
                                 nodeId, methodName );
            logger.error( err, e );
            throw new HmsException( err, e );
        }
        finally
        {
            try
            {
                nodeRateLimitModel.getThreadLimitExecuterServiceObject().getPool().invalidateObject( nodeId,
                                                                                                     threadLimitExecuterServiceObject );
            }
            catch ( Exception e )
            {
                logger.fatal( "Unable to return the threadLimitExecuterServiceObject to threadpool " + e );
            }
        }
    }
}
