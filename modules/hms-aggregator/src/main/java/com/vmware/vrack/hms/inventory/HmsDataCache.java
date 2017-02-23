/* ********************************************************************************
 * HmsDataCache.java
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

package com.vmware.vrack.hms.inventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.rest.model.CpuInfo;
import com.vmware.vrack.hms.common.rest.model.EthernetController;
import com.vmware.vrack.hms.common.rest.model.FruComponent;
import com.vmware.vrack.hms.common.rest.model.MemoryInfo;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.StorageController;
import com.vmware.vrack.hms.common.rest.model.StorageInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchPortInfo;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;

/**
 * HMS Data Cache.
 */
@Component
public class HmsDataCache
{

    private static Logger logger = LoggerFactory.getLogger( HmsDataCache.class );

    @Autowired
    private HostDataAggregator aggregator;

    @Value( "${hms.cache.flag}" )
    private boolean hmsCacheFlag;

    private Map<String, ServerInfo> serverInfoMap = new HashMap<String, ServerInfo>();

    private Map<String, NBSwitchInfo> switchInfoMap = new HashMap<String, NBSwitchInfo>();

    private static final ReentrantLock switchCacheUpdateLock = new ReentrantLock();

    /**
     * Create the HMS cache data when HMS Aggregator bootsup. hms.cache.flag by default set to false, meaning HMS will
     * not create the cache on Aggregator bootsup. hms.cache.flag set to true - HMS will create the cache on Aggregator
     * bootsup.
     */
    @PostConstruct
    public void createHMScache()
    {
        // Construct a HMS cache data when HMS Aggregator bootsup, if
        // hmsCacheFlag set to true
        if ( hmsCacheFlag == true )
        {

            Map<String, ServerNode> serverNodeMap = InventoryLoader.getInstance().getNodeMap();

            Iterator<Map.Entry<String, ServerNode>> entries = serverNodeMap.entrySet().iterator();
            while ( entries.hasNext() )
            {
                Map.Entry<String, ServerNode> entry = entries.next();
                try
                {
                    aggregator.getServerInfo( entry.getValue().getNodeID() );
                }
                catch ( HmsException e )
                {
                    logger.error( "Error while creating the HMS cache on HMS aggregator boot up", e );
                }
            }
        }
    }

    /**
     * Get the HMS server cache.
     *
     * @return Map<String, ServerInfo>
     */
    public Map<String, ServerInfo> getServerInfoMap()
    {
        return serverInfoMap;
    }

    /**
     * Set the HMS server cache.
     *
     * @param serverInfoMap the server info map
     */
    public void setServerInfoMap( Map<String, ServerInfo> serverInfoMap )
    {
        if ( serverInfoMap != null )
        {
            this.serverInfoMap = serverInfoMap;
        }
    }

    /**
     * Get the HMS switch cache.
     *
     * @return Map<String, NBSwitchInfo>
     */
    public Map<String, NBSwitchInfo> getSwitchInfoMap()
    {
        return switchInfoMap;
    }

    /**
     * Set the HMS switch cache.
     *
     * @param switchInfoMap the switch info map
     */
    public void setSwitchInfoMap( Map<String, NBSwitchInfo> switchInfoMap )
    {
        if ( switchInfoMap != null )
        {
            this.switchInfoMap = switchInfoMap;
        }
    }

    /**
     * Update the HMS Switch cache.
     *
     * @param nodeId the node id
     * @param component the component
     * @param fruComponent the fru component
     * @throws Exception the exception
     */
    public void updateHmsDataCache( String nodeId, ServerComponent component, FruComponent fruComponent )
        throws Exception
    {
        try
        {
            switch ( component )
            {
                case SERVER:
                    if ( fruComponent instanceof ServerInfo )
                    {
                        serverInfoMap.put( nodeId, (ServerInfo) fruComponent );
                    }
                    break;
                default:
                    break;
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while updating the HMS Switch cache for Node: {}", nodeId, e );
        }
    }

    /**
     * Update the HMS Switch cache.
     *
     * @param switchId the switch id
     * @param component the component
     * @param fruComponent the fru component
     * @throws Exception the exception
     */
    public void updateHmsSwitchDataCache( String switchId, SwitchComponentEnum component, FruComponent fruComponent )
        throws Exception
    {
        // Acquired the lock to update switch cache
        switchCacheUpdateLock.lock();
        logger.info( String.format( "Acquired the lock to update the Switch cache." ) );
        try
        {
            switch ( component )
            {
                case SWITCH:
                    if ( fruComponent instanceof NBSwitchInfo )
                        switchInfoMap.put( switchId, (NBSwitchInfo) fruComponent );
                    break;
                default:
                    break;
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while updating the HMS Switch cache for Switch Node: {}", switchId, e );
        }
        finally
        {
            // release the acquired lock for other thread to update switch cache
            switchCacheUpdateLock.unlock();
            logger.info( "Released the lock for other thread to update switch cache." );
        }
    }

    /**
     * Update the HMS Switch cache - Switch Port Information.
     *
     * @param switchID the switch id
     * @param portsList the ports list
     * @throws Exception the exception
     */
    public void updateHmsSwitchDataCachePortsConfig( String switchID, List<NBSwitchPortInfo> portsList )
        throws Exception
    {

        try
        {
            if ( switchInfoMap.get( switchID ) != null )
                switchInfoMap.get( switchID ).setPorts( portsList );
        }
        catch ( Exception e )
        {
            logger.error( "Error while updating the HMS Switch cache for switch Node for Switch Port Information: {}",
                          switchID, e );
        }
    }

    /**
     * Update the HMS Server FRU cache.
     *
     * @param node_id the node_id
     * @param component the component
     * @param fruComponent the fru component
     * @throws Exception the exception
     */
    @SuppressWarnings( { "unchecked", "deprecation" } )
    public void updateServerFruCache( String node_id, ServerComponent component, List<FruComponent> fruComponent )
        throws Exception
    {

        try
        {
            switch ( component )
            {
                case CPU:
                    if ( serverInfoMap.get( node_id ) != null )
                        serverInfoMap.get( node_id ).setCpuInfo( (List<CpuInfo>) (List<?>) fruComponent );
                    break;
                case STORAGE:
                    if ( serverInfoMap.get( node_id ) != null )
                        serverInfoMap.get( node_id ).setStorageInfo( (List<StorageInfo>) (List<?>) fruComponent );
                    break;
                case MEMORY:
                    if ( serverInfoMap.get( node_id ) != null )
                        serverInfoMap.get( node_id ).setMemoryInfo( (List<MemoryInfo>) (List<?>) fruComponent );
                    break;
                case NIC:
                    if ( serverInfoMap.get( node_id ) != null )
                        serverInfoMap.get( node_id ).setEthernetControllerList( (List<EthernetController>) (List<?>) fruComponent );
                    break;
                case STORAGE_CONTROLLER:
                    if ( serverInfoMap.get( node_id ) != null )
                        serverInfoMap.get( node_id ).setStorageController( (List<StorageController>) (List<?>) fruComponent );
                    break;
                default:
                    break;
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while updating the HMS server Fru cache for Node: " + node_id, e );
        }
    }

    /**
     * HMS Server Cash validation.
     *
     * @param serverNodes the serverNode
     * @return HmsCacheValidateEnum
     * @throws Exception the exception
     */
    public HmsCacheValidateEnum validateHostCache( Collection<ServerNode> serverNodes )
        throws Exception
    {

        try
        {
            if ( serverNodes != null && serverNodes.size() > 0 )
            {
                for ( ServerNode serverNode : serverNodes )
                {

                    if ( getServerInfoMap().get( serverNode.getNodeID() ) != null )
                    {
                        ServerInfo serverInfo = getServerInfoMap().get( serverNode.getNodeID() );
                        if ( serverInfo == null )
                        {
                            logger.debug( "Server Info is null for node {}, In memory Host cache data has not been built for it.",
                                          serverNode.getNodeID() );
                            return HmsCacheValidateEnum.INVALID;
                        }
                    }
                    else
                    {
                        logger.warn( "Couldn't get the HMS In memory host cache data for the node: {} cache has not built",
                                     serverNode.getNodeID() );
                        return HmsCacheValidateEnum.INVALID;
                    }
                }

                logger.debug( "HmsDataCache: HMS In memory Host cache has built for all the nodes in the inventory for {} ServerNodes",
                              serverNodes.size() );
                return HmsCacheValidateEnum.VALID;

            }
            else
            {
                logger.warn( "HmsDataCache: Failed to validate as the serverNodes are NULL or the size is zero. Returning HMS cache for ServerNodes as invalid." );
                return HmsCacheValidateEnum.INVALID;
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while validating the HMS server cache", e );
            throw e;
        }
    }

    /**
     * HMS Switch Cash validation.
     *
     * @param switches the switches
     * @return HmsCacheValidateEnum
     * @throws Exception the exception
     */
    public HmsCacheValidateEnum validateSwitchCache( Object[] switches )
        throws Exception
    {

        try
        {
            if ( switches != null && switches.length > 0 )
            {
                for ( int i = 0; i < switches.length; i++ )
                {
                    Map<String, String> switchMap = (Map<String, String>) switches[i];

                    if ( getSwitchInfoMap().get( switchMap.get( "switchId" ) ) != null )
                    {
                        NBSwitchInfo switchInfo = getSwitchInfoMap().get( switchMap.get( "switchId" ) );
                        if ( switchInfo == null )
                        {
                            logger.debug( "Switch Info is null for the switch node: {}. HMS In memory Switch cache data has not built for it.",
                                          switchMap.get( "switchId" ) );
                            return HmsCacheValidateEnum.INVALID;
                        }
                    }
                    else
                    {
                        logger.debug( "Couldn't get the HMS In memory Switch cache data for the switch node: {}. Cache has not built",
                                      switchMap.get( "switchId" ) );
                        return HmsCacheValidateEnum.INVALID;
                    }
                }

                logger.debug( "HmsDataCache: HMS In memory Switch cache has built for all the switch nodes in the inventory. Returning HMS cache for Switches as Valid." );
                return HmsCacheValidateEnum.VALID;

            }
            else
            {
                logger.warn( "HmsDataCache: Failed to validate as the switches list is NULL or the size is zero. Returning HMS cache for Switches as Invalid." );
                return HmsCacheValidateEnum.INVALID;
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while validating the HMS Switch cache", e );
            throw e;
        }
    }

    /**
     * Remover server.
     *
     * @param hostId the host id
     * @return the server info
     */
    public ServerInfo removerServer( final String hostId )
    {
        if ( hostId != null && this.serverInfoMap.containsKey( hostId ) )
        {
            ServerInfo serverInfo = this.serverInfoMap.remove( hostId );
            if ( serverInfo != null && StringUtils.equals( hostId, serverInfo.getNodeId() ) )
            {
                logger.info( "In removerServer, removed host with hostId '{}' from cache.", hostId );
                return serverInfo;
            }
            else
            {
                logger.warn( "In removerServer, Server Info Map contains key with hostId '{}', "
                    + "but contains a ServerInfo object with NodeId '{}'.", hostId, serverInfo.getNodeId() );
            }
        }
        else
        {
            logger.error( "In removerServer, host with hostId '{}' not found in cache.", hostId );
        }
        return null;
    }
}
