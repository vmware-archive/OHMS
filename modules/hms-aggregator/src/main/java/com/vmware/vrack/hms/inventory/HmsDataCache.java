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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
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
 * HMS Data Cache
 */
@Component
public class HmsDataCache
{
    private static Logger logger = Logger.getLogger( HmsDataCache.class );

    @Autowired
    private HostDataAggregator aggregator;

    @Value( "${hms.cache.flag}" )
    private boolean hmsCacheFlag;

    private Map<String, ServerInfo> serverInfoMap = new HashMap<String, ServerInfo>();

    private Map<String, NBSwitchInfo> switchInfoMap = new HashMap<String, NBSwitchInfo>();

    private ReentrantLock switchCacheUpdateLock = new ReentrantLock();

    /**
     * Create the HMS cache data when HMS Aggregator bootsup. hms.cache.flag by default set to false, meaning HMS will
     * not create the cache on Aggregator bootsup. hms.cache.flag set to true - HMS will create the cache on Aggregator
     * bootsup.
     */
    @PostConstruct
    public void createHMScache()
    {
        // Construct a HMS cache data when HMS Aggregator bootsup, if hmsCacheFlag set to true
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
     * Get the HMS server cache
     *
     * @return Map<String, ServerInfo>
     */
    public Map<String, ServerInfo> getServerInfoMap()
    {
        return serverInfoMap;
    }

    /**
     * Set the HMS server cache
     *
     * @param serverInfoMap
     */
    public void setServerInfoMap( Map<String, ServerInfo> serverInfoMap )
    {
        this.serverInfoMap = serverInfoMap;
    }

    /**
     * Get the HMS switch cache
     *
     * @return Map<String, NBSwitchInfo>
     */
    public Map<String, NBSwitchInfo> getSwitchInfoMap()
    {
        return switchInfoMap;
    }

    /**
     * Set the HMS switch cache
     *
     * @param switchInfoMap
     */
    public void setSwitchInfoMap( Map<String, NBSwitchInfo> switchInfoMap )
    {
        this.switchInfoMap = switchInfoMap;
    }

    /**
     * Update the HMS Switch cache
     *
     * @param node_id
     * @param component
     * @param fruComponent
     * @throws Exception
     */
    public void updateHmsDataCache( String node_id, ServerComponent component, FruComponent fruComponent )
        throws Exception
    {
        try
        {
            switch ( component )
            {
                case SERVER:
                    if ( fruComponent instanceof ServerInfo )
                        serverInfoMap.put( node_id, (ServerInfo) fruComponent );
                    break;
                default:
                    break;
            }
        }
        catch ( Exception e )
        {
            logger.error( "Error while updating the HMS Switch cache for Node: " + node_id, e );
        }
    }

    /**
     * Update the HMS Switch cache
     *
     * @param switchId
     * @param component
     * @param fruComponent
     * @throws Exception
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
            logger.error( "Error while updating the HMS Switch cache for Switch Node: " + switchId, e );
        }
        finally
        {
            // release the acquired lock for other thread to update switch cache
            switchCacheUpdateLock.unlock();
            logger.info( "Released the lock for other thread to update switch cache." );
        }
    }

    /**
     * Update the HMS Switch cache - Switch Port Information
     *
     * @param switchID
     * @param portsList
     * @throws Exception
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
            logger.error( "Error while updating the HMS Switch cache for switch Node for Switch Port Information: "
                + switchID, e );
        }
    }

    /**
     * Update the HMS Server FRU cache
     *
     * @param node_id
     * @param component
     * @param fruComponent
     * @throws Exception
     */
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
     * HMS Server Cash validation
     *
     * @param hosts
     * @return HmsCacheValidateEnum
     * @throws Exception
     */
    public HmsCacheValidateEnum validateHostCache( Object[] hosts )
        throws Exception
    {
        try
        {
            for ( int i = 0; i < hosts.length; i++ )
            {
                ServerNode serverNode = (ServerNode) hosts[i];
                if ( getServerInfoMap().get( serverNode.getNodeID() ) != null )
                {
                    ServerInfo serverInfo = getServerInfoMap().get( serverNode.getNodeID() );
                    if ( serverInfo == null )
                    {
                        return HmsCacheValidateEnum.INVALID;
                    }
                }
                else
                {
                    return HmsCacheValidateEnum.INVALID;
                }
            }
            if ( hosts != null )
                return HmsCacheValidateEnum.VALID;
        }
        catch ( Exception e )
        {
            logger.error( "Error while validating the HMS server cache", e );
        }
        return null;
    }

    /**
     * HMS Switch Cash validation
     *
     * @param switches
     * @return HmsCacheValidateEnum
     * @throws Exception
     */
    public HmsCacheValidateEnum validateSwitchCache( Object[] switches )
        throws Exception
    {
        try
        {
            for ( int i = 0; i < switches.length; i++ )
            {
                Map<String, String> switchMap = (Map<String, String>) switches[i];
                if ( getSwitchInfoMap().get( switchMap.get( "switchId" ) ) != null )
                {
                    NBSwitchInfo switchInfo = getSwitchInfoMap().get( switchMap.get( "switchId" ) );
                    if ( switchInfo == null )
                    {
                        return HmsCacheValidateEnum.INVALID;
                    }
                }
                else
                {
                    return HmsCacheValidateEnum.INVALID;
                }
            }
            if ( switches != null )
                return HmsCacheValidateEnum.VALID;
        }
        catch ( Exception e )
        {
            logger.error( "Error while validating the HMS Switch cache", e );
        }
        return null;
    }
}
