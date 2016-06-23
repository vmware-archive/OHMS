/* ********************************************************************************
 * BoardServiceProvider.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.boardservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.HmsNode;
import com.vmware.vrack.hms.common.boardvendorservice.api.BoardServiceFactory;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.component.lifecycle.api.IComponentLifecycleManager;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.BoardInfo;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;

/**
 * Factory class to provide Board Service Instance for Service Server Node. Board Service instance can also be cached
 * and retrieved at later stage if required.
 *
 * @author Yagnesh Chawda
 */
public class BoardServiceProvider
{
    private static Logger logger = Logger.getLogger( BoardServiceProvider.class );

    // Key = Node Id, Value = IBoardService
    private static Map<String, Class> cachedBoardServiceClasses = new ConcurrentHashMap<String, Class>();

    private static Map<String, List<HmsApi>> operationSupported = new ConcurrentHashMap<String, List<HmsApi>>();

    private static Map<String, IComponentLifecycleManager> componentLifecycleManagerInstanceMap =
        new ConcurrentHashMap<String, IComponentLifecycleManager>();

    private static Class<?> componentLifecycleManagerClass = IComponentLifecycleManager.class;

    /**
     * Method to get cached Board Service from Board Service Factory
     *
     * @param serviceHmsNode
     * @param cached
     * @return
     * @throws Exception
     */
    public static IBoardService getBoardService( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        if ( serviceHmsNode != null )
        {
            try
            {
                IBoardService boardService = null;
                Class<?> boardServiceClass = cachedBoardServiceClasses.get( serviceHmsNode.getNodeID() );
                if ( boardServiceClass == null )
                {
                    throw new HmsException( "No Board service found for Node:" + serviceHmsNode.getNodeID() );
                }
                else
                {
                    try
                    {
                        boardService = (IBoardService) boardServiceClass.newInstance();
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Error while getting IBoardService  for Node:" + serviceHmsNode.getNodeID(), e );
                        throw new HmsException( e );
                    }
                }
                return boardService;
            }
            catch ( HmsException e )
            {
                logger.error( "Error while getting Board Service for Node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
        }
        else
        {
            throw new HmsException( "Service HMS Node is NULL" );
        }
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

    /**
     * Add a BoardService Object into the cache for later user use against key = NodeId. If overwrite = false, it will
     * not replace existing IBoardService if it is already present in cachedBoardServices
     *
     * @param serviceHmsNode
     * @param boardServiceClass
     * @return
     * @throws Exception
     */
    public static boolean addBoardServiceClass( ServiceHmsNode serviceHmsNode, Class<?> boardServiceClass,
                                                boolean overwrite )
                                                    throws Exception
    {
        if ( serviceHmsNode == null )
        {
            throw new HmsException( "Service Hms Node can Not be Null" );
        }
        if ( boardServiceClass == null )
        {
            throw new HmsException( "IBoardService class can Not be Null" );
        }
        String nodeId = serviceHmsNode.getNodeID();
        if ( nodeId != null )
        {
            if ( overwrite || cachedBoardServiceClasses.get( nodeId ) == null )
            {
                cachedBoardServiceClasses.put( serviceHmsNode.getNodeID(), boardServiceClass );
                operationSupported.put( serviceHmsNode.getNodeID(), getSupportedHMSApi( serviceHmsNode ) );
            }
        }
        else
        {
            throw new HmsException( "Node Id should NOT be null while adding in cached Board service" );
        }
        return true;
    }

    /**
     * Prepare mapping of Node to Board Service Implementation in the cache, for only given list of BoardService
     * Implementations
     *
     * @param hmsNodes
     * @return
     * @throws Exception
     */
    public static boolean prepareBoardServiceClassesForNodes( List<HmsNode> hmsNodes )
        throws Exception
    {
        BoardServiceFactory boardServiceFactory = BoardServiceFactory.getBoardServiceFactory();
        if ( hmsNodes != null )
        {
            for ( HmsNode hmsNode : hmsNodes )
            {
                if ( hmsNode != null )
                {
                    ServerNode serverNode = (ServerNode) hmsNode;
                    BoardInfo boardInfo = new BoardInfo();
                    boardInfo.setBoardManufacturer( serverNode.getBoardVendor() );
                    boardInfo.setBoardProductName( serverNode.getBoardProductName() );
                    String boardServiceKey = BoardServiceFactory.getBoardServiceKey( boardInfo );
                    Class<?> boardServiceClass = boardServiceFactory.getBoardServiceClass( boardServiceKey );
                    if ( boardServiceClass != null )
                    {
                        try
                        {
                            BoardServiceProvider.addBoardServiceClass( serverNode.getServiceObject(), boardServiceClass,
                                                                       false );
                            if ( componentLifecycleManagerClass.isAssignableFrom( boardServiceClass ) )
                            {
                                componentLifecycleManagerInstanceMap.put( serverNode.getServiceObject().getNodeID(),
                                                                          (IComponentLifecycleManager) getBoardService( serverNode.getServiceObject() ) );
                            }
                            HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( serverNode.getNodeID() );
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
                            logger.error( "Exception while adding Board Service into BoardServiceProvider:"
                                + boardServiceClass, e );
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Method to get operations supported by board service
     *
     * @param serviceHmsNode
     * @param cached
     * @return
     * @throws Exception
     */
    public static List<HmsApi> getSupportedHMSApi( ServiceHmsNode serviceHmsNode )
        throws Exception
    {
        if ( serviceHmsNode != null )
        {
            try
            {
                IBoardService boardService = getBoardService( serviceHmsNode );
                if ( boardService == null )
                {
                    throw new HmsException( "Unable to get Board Service instance for Node "
                        + serviceHmsNode.getNodeID() );
                }
                return boardService.getSupportedHmsApi( serviceHmsNode );
            }
            catch ( HmsException e )
            {
                logger.error( "Error while getting Board Service for Node:" + serviceHmsNode.getNodeID(), e );
                throw e;
            }
        }
        else
        {
            throw new HmsException( "Service HMS Node is NULL" );
        }
    }

    public static Map<String, List<HmsApi>> getOperationSupported()
    {
        return operationSupported;
    }

    public static void setOperationSupported( Map<String, List<HmsApi>> operationSupported )
    {
        BoardServiceProvider.operationSupported = operationSupported;
    }

    /**
     * Method to remove board service for particular node
     *
     * @param node
     * @return
     */
    public static boolean removeBoardServiceClass( ServiceHmsNode node )
    {
        if ( node != null )
        {
            logger.debug( "Removing board service for node [ " + node.getNodeID() + " ]" );
            cachedBoardServiceClasses.remove( node.getNodeID() );
            return true;
        }
        else
        {
            logger.error( "Cannot remove null node. Provide right node." );
            return false;
        }
    }
}
