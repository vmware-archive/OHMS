/* ********************************************************************************
 * InBandServiceProvider.java
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
package com.vmware.vrack.hms.service.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.api.ib.HypervisorInfo;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.IInbandService;
import com.vmware.vrack.hms.common.boardvendorservice.api.ib.InBandServiceFactory;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.component.lifecycle.api.IComponentLifecycleManager;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Factory class to provide InBand Service Instance for Service Server Node. In Band Service instance can also be cached
 * and retrieved at later stage if required.
 */
public class InBandServiceProvider
{
    private static Logger logger = Logger.getLogger( InBandServiceProvider.class );

    // Key = Node Id, Value = IBoardService
    private static Map<String, IInbandService> cachedBoardServices = new HashMap<String, IInbandService>();

    /** The component lifecycle manager class. */
    private static Class<?> componentLifecycleManagerClass = IComponentLifecycleManager.class;

    private static Map<String, IComponentLifecycleManager> componentLifecycleManagerInstanceMap =
        new ConcurrentHashMap<String, IComponentLifecycleManager>();

    /**
     * Method to get cached Board Service from Board Service Factory
     *
     * @param serviceHmsNode
     * @param cached
     * @return
     * @throws HmsException
     */
    public static IInbandService getBoardService( ServiceHmsNode serviceHmsNode )
        throws HmsException
    {
        if ( serviceHmsNode != null )
        {
            try
            {
                IInbandService boardService = null;
                boardService = cachedBoardServices.get( serviceHmsNode.getNodeID() );
                if ( boardService == null )
                {
                    /*
                     * List<ServiceHmsNode> serviceHmsNodes = new ArrayList<ServiceHmsNode>();
                     * serviceHmsNodes.add(serviceHmsNode); prepareBoardServiceForNodes(serviceHmsNodes); boardService =
                     * cachedBoardServices.get(serviceHmsNode.getNodeID()); if(boardService == null) {
                     */
                    throw new HmsException( "No InBand service found for Node:" + serviceHmsNode.getNodeID() );
                    // }
                }
                return boardService;
            }
            catch ( HmsException e )
            {
                logger.error( "Error while getting InBand Service for Node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
        }
        else
        {
            throw new HmsException( "Service HMS Node is NULL" );
        }
    }

    /**
     * Add a BoardService Object into the cache for later user use against key = NodeId. If overwrite = false, it will
     * not replace existing IBoardService if it is already present in cachedBoardServices
     *
     * @param serviceHmsNode
     * @param boardService
     * @return
     * @throws HmsException
     */
    public static boolean addBoardService( ServiceHmsNode serviceHmsNode, IInbandService boardService,
                                           boolean overwrite )
                                               throws HmsException
    {
        if ( serviceHmsNode == null )
        {
            throw new HmsException( "Service Hms Node can Not be Null" );
        }
        if ( boardService == null )
        {
            throw new HmsException( "IInBandService can Not be Null" );
        }
        String nodeId = serviceHmsNode.getNodeID();
        if ( nodeId != null )
        {
            if ( overwrite || cachedBoardServices.get( nodeId ) == null )
            {
                cachedBoardServices.put( serviceHmsNode.getNodeID(), boardService );
            }
        }
        else
        {
            throw new HmsException( "Node Id should NOT be null while adding in cached InBand service" );
        }
        return true;
    }

    /**
     * Prepare mapping of Node to Board Service Implementation in the cache
     *
     * @param serviceHmsNodes
     * @return
     */
    /*
     * public static boolean prepareBoardServiceForNodes(List<ServiceHmsNode> serviceHmsNodes) { BoardServiceFactory
     * boardServiceFactory = BoardServiceFactory.getBoardServiceFactory(); List<Class<?>> boardServices =
     * boardServiceFactory.getBoardServiceImplementationClasses(); prepareBoardServiceForNodes(serviceHmsNodes,
     * boardServices); return true; }
     */
    /**
     * Prepare mapping of Node to Board Service Implementation in the cache, for only given list of BoardService
     * Implementations
     *
     * @param hmsNodes
     * @return
     */
    public static boolean prepareBoardServiceForNodes( Collection<ServerNode> hmsNodes )
    {
        return prepareBoardServiceForNodes( hmsNodes, false );
    }

    /**
     * Prepare mapping of Node to Board Service Implementation in the cache, for only given list of BoardService
     * Implementations With the ability to force overwrite the Inband Provider
     *
     * @param hmsNodes
     * @param overwrite
     * @return
     */
    public static boolean prepareBoardServiceForNodes( Collection<ServerNode> hmsNodes, boolean overwrite )
    {
        InBandServiceFactory boardServiceFactory = InBandServiceFactory.getBoardServiceFactory();
        if ( hmsNodes != null )
        {
            for ( ServerNode hmsNode : hmsNodes )
            {
                if ( hmsNode != null )
                {
                    ServerNode serverNode = hmsNode;
                    HypervisorInfo boardInfo = new HypervisorInfo();
                    boardInfo.setName( serverNode.getHypervisorName() );
                    boardInfo.setProvider( serverNode.getHypervisorProvider() );
                    String boardServiceKey = InBandServiceFactory.getBoardServiceKey( boardInfo );
                    Class<?> boardServiceClass = boardServiceFactory.getBoardServiceClass( boardServiceKey );
                    if ( boardServiceClass != null )
                    {
                        try
                        {
                            ServiceHmsNode serviceHmsNode = serverNode.getServiceObject();
                            IInbandService boardService = (IInbandService) boardServiceClass.newInstance();
                            InBandServiceProvider.addBoardService( serviceHmsNode, boardService, overwrite );
                            // Getting boardService from InBandServiceProvider, because, local copy we got just now,
                            // might not have got over-written if overwrite = false.
                            boardService = InBandServiceProvider.getBoardService( serviceHmsNode );
                            if ( boardService != null )
                            {
                                boardService.init( serviceHmsNode );
                            }
                            else
                            {
                                logger.error( "Unable to reinitialize Inband Service for node: "
                                    + serverNode.getNodeID() );
                            }
                            if ( componentLifecycleManagerClass.isAssignableFrom( boardServiceClass ) )
                            {
                                componentLifecycleManagerInstanceMap.put( serviceHmsNode.getNodeID(),
                                                                          (IComponentLifecycleManager) boardService );
                            }
                        }
                        catch ( InstantiationException e )
                        {
                            logger.error( "Exception during creating new Instance of class:" + boardServiceClass, e );
                        }
                        catch ( IllegalAccessException e )
                        {
                            logger.error( "Exception during creating new Instance of class:" + boardServiceClass, e );
                        }
                        catch ( HmsException e )
                        {
                            logger.error( "Exception while adding InBand Service into InBandServiceProvider:"
                                + boardServiceClass, e );
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Gets the component lifecycle manager instance.
     *
     * @param serviceHmsNode the service hms node
     * @return the component lifecycle manager instance
     */
    public static IComponentLifecycleManager getComponentLifecycleManagerInstance( ServiceHmsNode serviceHmsNode )
    {
        return componentLifecycleManagerInstanceMap.get( serviceHmsNode.getNodeID() );
    }

    /**
     * Gets the component lifecycle manager instances.
     *
     * @return the component lifecycle manager instances
     */
    public static List<IComponentLifecycleManager> getComponentLifecycleManagerInstances()
    {
        if ( !componentLifecycleManagerInstanceMap.isEmpty() )
        {
            List<IComponentLifecycleManager> instances = new ArrayList<IComponentLifecycleManager>();
            instances.addAll( componentLifecycleManagerInstanceMap.values() );
            return instances;
        }
        return null;
    }
}
