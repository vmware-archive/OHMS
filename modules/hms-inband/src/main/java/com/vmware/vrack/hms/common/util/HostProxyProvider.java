/* ********************************************************************************
 * HostProxyProvider.java
 * 
 * Copyright (C) 2014-2016 VMware, Inc. - All rights reserved.
 *
 * *******************************************************************************/
package com.vmware.vrack.hms.common.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceServerNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.vsphere.HostManager;
import com.vmware.vrack.hms.vsphere.HostProxy;

/**
 * HostProxy Provider class, that will facilitate the caching and reusing of HostProxy object for each node, and when
 * the hostProxy is requested, but is no longer valid, it will create a new HostProxy object, cache it and return it.
 *
 * @author Vmware
 */
public class HostProxyProvider
{
    private static Logger logger = Logger.getLogger( HostProxyProvider.class );

    private Map<HostIdentifier, HostProxy> hostProxyMap = new ConcurrentHashMap<HostIdentifier, HostProxy>();

    private static HostProxyProvider hostProxyProvider = null;

    private Map<String, Object> locksMap = new ConcurrentHashMap<String, Object>();

    private HostProxyProvider()
    {
    }

    public static HostProxyProvider getInstance()
    {
        if ( hostProxyProvider == null )
        {
            hostProxyProvider = new HostProxyProvider();
        }
        return hostProxyProvider;
    }

    /**
     * Gets HostProxy object for the node. Will try to look for the already cached hostProxy object. If the cached
     * version is not valid anymore, creates a new HostProxy object and caches it
     * 
     * @param node
     * @return
     * @throws HmsException
     */
    public HostProxy getHostProxy( ServiceServerNode node )
        throws HmsException
    {
        if ( node != null )
        {
            HostIdentifier hostIdentifier = new HostIdentifier( node.getNodeID(), node.getIbIpAddress(),
                                                                node.getOsUserName(), node.getOsPassword() );
            synchronized ( this )
            {
                // If HostProxy is NOT available, then create Object for that node, so that later in this method, it can
                // be used as Lock.
                if ( !hostProxyMap.containsKey( hostIdentifier ) )
                {
                    if ( !locksMap.containsKey( node.getNodeID() ) )
                    {
                        locksMap.put( node.getNodeID(), new Object() );
                    }
                }
            }
            // Host proxy is NOT there or NOT valid.
            // Try to create HostProxy in Synchronous way, but will be synchronized only for same node.
            // If call comes for another Host, it creating HostProxy for that should NOT get blocked.
            Object lockObj = locksMap.get( node.getNodeID() );
            synchronized ( lockObj )
            {
                if ( hostProxyMap.containsKey( hostIdentifier ) )
                {
                    HostProxy cachedHostProxy = hostProxyMap.get( hostIdentifier );
                    if ( isHostProxyValid( cachedHostProxy ) )
                    {
                        return cachedHostProxy;
                    }
                }
                else
                {
                    removeStaleProxy( hostIdentifier );
                }
                HostProxy hostProxy = createHostProxy( node );
                hostProxyMap.put( hostIdentifier, hostProxy );
                return hostProxy;
            }
        }
        else
        {
            String err = "Cannot get HostProxy for Null Node: " + node;
            logger.error( err );
            throw new HmsException( err );
        }
    }

    /**
     * Remove stale Host Proxy, in case Ip reconfig happened, and old Host Proxy is still in Map
     * 
     * @param hostIdentifier
     */
    private void removeStaleProxy( HostIdentifier hostIdentifier )
    {
        if ( hostIdentifier != null )
        {
            String nodeId = hostIdentifier.getNodeId();
            for ( HostIdentifier id : hostProxyMap.keySet() )
            {
                if ( nodeId != null && nodeId.equals( id.getNodeId() ) )
                {
                    hostProxyMap.remove( id );
                }
            }
        }
    }

    /**
     * Removes a particular HostProxy object
     * 
     * @param id
     */
    public void removeHostProxy( HostIdentifier id )
    {
        hostProxyMap.remove( id );
    }

    /**
     * Removes a particular HostProxy object
     */
    public void removeAllHostProxies()
    {
        for ( HostIdentifier id : hostProxyMap.keySet() )
        {
            removeHostProxy( id );
        }
    }

    /**
     * Check if the cached HostProxy object is valid anymore.
     * 
     * @param hostProxy
     * @return
     */
    public static boolean isHostProxyValid( HostProxy hostProxy )
    {
        if ( hostProxy != null )
        {
            try
            {
                if ( hostProxy.getHostSystem() != null && hostProxy.getHostSystem().getHardware() != null )
                {
                    return true;
                }
            }
            catch ( Exception e )
            {
                logger.error( "HostProxy is no longer valid", e );
            }
        }
        return false;
    }

    /**
     * Create new HostProxy object from node credentials
     * 
     * @param node
     * @return
     * @throws HmsException
     */
    private HostProxy createHostProxy( ServiceServerNode node )
        throws HmsException
    {
        if ( node != null )
        {
            try
            {
                HostProxy hostProxy = HostManager.getInstance().connect( node.getIbIpAddress(), node.getOsUserName(),
                                                                         node.getOsPassword() );
                return hostProxy;
            }
            catch ( Exception e )
            {
                String err = "Cannot create HostProxy Object for node: " + node.getNodeID();
                logger.error( err, e );
                throw new HmsException( err, e );
            }
        }
        else
        {
            String err = "Cannot create HostProxy object for node: " + node;
            logger.error( err );
            throw new HmsException( err );
        }
    }

    /**
     * Utility method to set existing HostProxy object to the Map, that caches HostProxy objects
     * 
     * @param node
     * @param hostProxy
     * @return
     * @throws HmsException
     */
    public boolean setHostProxyforNode( ServiceServerNode node, HostProxy hostProxy )
        throws HmsException
    {
        if ( node != null && hostProxy != null )
        {
            HostIdentifier identifier = new HostIdentifier( node.getNodeID(), node.getIbIpAddress(),
                                                            node.getOsUserName(), node.getOsPassword() );
            logger.debug( "Hostproxy object for node [ " + node.getNodeID()
                + " ] is not null. Now checking if the hostProxy object is valid or not." );
            if ( isHostProxyValid( hostProxy ) )
            {
                logger.debug( "Hostproxy object for node [ " + node.getNodeID()
                    + " ] is valid and can be added to the HostProxyMap." );
                hostProxyMap.put( identifier, hostProxy );
                logger.debug( "Hostproxy object for node [ " + node.getNodeID()
                    + " ] was successfully addded to HostProxyMap." );
                return true;
            }
            else
            {
                String err = "The hostProxy that you are trying to add for node [ " + node.getNodeID()
                    + " ] is not valid anymore. Please try adding a new HostProxy object. ";
                logger.error( err );
                throw new HmsException( err );
            }
        }
        else
        {
            String err = "Unable to inject new host proxy [ " + hostProxy + " ] for node [ " + node + " ]";
            logger.error( err );
            throw new HmsException( err );
        }
    }
}
