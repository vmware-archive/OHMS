/* ********************************************************************************
 * MergeDataUtil.java
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

package com.vmware.vrack.hms.aggregator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

public class MergeDataUtil
{
    private static Logger logger = LoggerFactory.getLogger( MergeDataUtil.class );

    /**
     * Will be called when /nodes will be called in hms-aggregator
     * 
     * @param oobData
     * @param ibData
     * @throws HmsException
     */
    public static void mergeServerNodeDataInOobOperation( ServerNode oobData, ServerNode ibData )
        throws HmsException
    {
        if ( ibData != null )
        {
            mergeInbandData( oobData, ibData, false );

            List<CPUInfo> cpuInfo = ibData.getCpuInfo();
            List<PhysicalMemory> physicalMemories = ibData.getPhysicalMemoryInfo();
            List<HddInfo> hddInfo = ibData.getHddInfo();
            List<EthernetController> nicInfo = ibData.getEthernetControllerList();
            List<StorageControllerInfo> storageControllerInfo = ibData.getStorageControllerInfo();

            oobData.setCpuInfo( cpuInfo );
            oobData.setPhysicalMemoryInfo( physicalMemories );
            oobData.setHddInfo( hddInfo );
            oobData.setEthernetControllerList( nicInfo );
            oobData.setStorageControllerInfo( storageControllerInfo );
        }

        InventoryLoader.getInstance().getNodeMap().put( oobData.getNodeID(), oobData );
    }

    /**
     * Will be called when /refreshinventory at HMS bootup happens in hms-aggregator
     * 
     * @param inventoryData
     * @param inMemoryData
     * @throws HmsException
     */
    public static void mergeServerNodeDataInIbOperation( ServerNode inventoryData, ServerNode inMemoryData )
        throws HmsException
    {
        if ( inventoryData != null && inMemoryData != null )
        {
            mergeInbandData( inMemoryData, inventoryData, true );
            InventoryLoader.getInstance().getNodeMap().put( inMemoryData.getNodeID(), inMemoryData );
        }
        else if ( inventoryData != null && inMemoryData == null )
        {
            InventoryLoader.getInstance().getNodeMap().put( inventoryData.getNodeID(), inventoryData );

            // Re-map node with the updated Hypervisor Providor
            List<ServerNode> servers = new ArrayList<ServerNode>();
            servers.add( inventoryData );

            InBandServiceProvider.prepareBoardServiceForNodes( servers, true );

        }
    }

    /**
     * Merge Updated Inband data with the current inventory
     * 
     * @param target
     * @param source
     * @param prepareBoardService
     * @throws HmsException
     */
    public static void mergeInbandData( ServerNode target, ServerNode source, boolean prepareBoardService )
        throws HmsException
    {
        if ( target == null )
        {
            logger.warn( "Target Server Node object should not be NULL" );
            return;
        }

        if ( source != null )
        {
            String ibIpAddressPriorChange = target.getIbIpAddress();
            String osUserNamePriorChange = target.getOsUserName();
            String osPasswordPriorChange = target.getOsPassword();

            target.setIbIpAddress( source.getIbIpAddress() );
            target.setOsUserName( source.getOsUserName() );
            target.setOsPassword( source.getOsPassword() );
            target.setOsEncodedPassword( source.getOsEncodedPassword() );

            // Check if node's HyperVisor has been changed
            if ( source.getHypervisorName() != null && source.getHypervisorProvider() != null
                && ( !source.getHypervisorName().equals( target.getHypervisorName() )
                    || !source.getHypervisorProvider().equals( target.getHypervisorProvider() ) ) )
            {
                target.setHypervisorName( source.getHypervisorName() );
                target.setHypervisorProvider( source.getHypervisorProvider() );

                // Re-map node with the updated Hypervisor Providor
                List<ServerNode> servers = new ArrayList<ServerNode>();
                servers.add( source );

                if ( prepareBoardService )
                {
                    InBandServiceProvider.prepareBoardServiceForNodes( servers, true );
                }
            }
            else if ( prepareBoardService
                && ( ( ibIpAddressPriorChange != null && !ibIpAddressPriorChange.equals( target.getIbIpAddress() ) )
                    || ( osUserNamePriorChange != null && !osUserNamePriorChange.equals( target.getOsUserName() ) )
                    || ( osPasswordPriorChange != null && !osPasswordPriorChange.equals( target.getOsPassword() ) ) ) )
            {
                // Don't want to overwrite existing IB ServiceObject, because Hypervisor object has NOT been changed.
                // But, we want to re-initialize the Service to let it know if it needs to re-init its own cache for
                // this node.
                List<ServerNode> servers = new ArrayList<ServerNode>();
                servers.add( source );
                InBandServiceProvider.prepareBoardServiceForNodes( servers, false );
            }

            return;
        }
        else
        {
            logger.warn( "Source ServerNode object NOT found for OOB node ID: " );
            return;
        }
    }

    /**
     * Refreshes Node data with the latest information, plus the ability to refresh Inband data such as Inband Ip
     * Address etc
     * 
     * @param data
     * @param mergeInBandData
     * @throws HmsException
     */
    public static void refreshNodeData( Map<String, Object[]> data, boolean isDataFromHmsOob )
        throws HmsException
    {
        if ( data != null && data.containsKey( Constants.HOSTS ) )
        {
            Object[] hosts = data.get( Constants.HOSTS );
            if ( hosts != null && hosts.length > 0 )
            {
                ObjectMapper mapper = new ObjectMapper();
                ServerNode[] nodes = mapper.convertValue( hosts, new TypeReference<ServerNode[]>()
                {
                } );
                for ( ServerNode host : nodes )
                {
                    logger.debug( "Getting node for nodeId: " + host.getNodeID() + " :"
                        + InventoryLoader.getInstance().getNodeMap().get( host.getNodeID() ) );

                    if ( isDataFromHmsOob )
                    {
                        mergeServerNodeDataInOobOperation( host,
                                                           InventoryLoader.getInstance().getNodeMap().get( host.getNodeID() ) );
                    }
                    else
                    {
                        mergeServerNodeDataInIbOperation( host,
                                                          InventoryLoader.getInstance().getNodeMap().get( host.getNodeID() ) );
                    }
                }
            }
            else
            {
                logger.warn( "No Hosts  found while refreshing Node Data." );
            }
        }

        if ( data != null && data.containsKey( Constants.SWITCHES ) )
        {
            Object[] switches = data.get( Constants.SWITCHES );
            if ( switches != null && switches.length > 0 )
            {
                ObjectMapper mapper = new ObjectMapper();
                SwitchNode[] switchNodes = mapper.convertValue( switches, new TypeReference<SwitchNode[]>()
                {
                } );
                for ( SwitchNode switchNode : switchNodes )
                {
                    InventoryLoader.getInstance().getSwitchNodeMap().put( switchNode.getSwitchId(), switchNode );
                }
            }
        }
        else
        {
            logger.warn( "No switches found while refreshing Node Data." );
        }
        // Creates the HMS Inventory switch data (In switch Inventory loader) from HMS IB inventory file data
        if ( data != null && data.containsKey( Constants.SWITCHES ) )
        {
            Object[] switches = data.get( Constants.SWITCHES );
            if ( switches != null && switches.length > 0 )
            {
                ObjectMapper mapper = new ObjectMapper();
                SwitchNode[] switchNodes = mapper.convertValue( switches, new TypeReference<SwitchNode[]>()
                {
                } );
                for ( SwitchNode switchNode : switchNodes )
                {
                    logger.debug( "Getting node details for switch nodeId: {} ", switchNode.getSwitchId() + " : {}",
                                  InventoryLoader.getInstance().getSwitchNodeMap().get( switchNode.getSwitchId() ) );

                    mergeSwitchNodeData( switchNode,
                                         InventoryLoader.getInstance().getSwitchNodeMap().get( switchNode.getSwitchId() ) );
                }
            }
            else
            {
                logger.warn( "No switch nodes found while update Inventory loader switch Node Data." );
            }
        }
    }

    /**
     * Create/update the HMS Inventory switch data - In switch Inventory loader
     * 
     * @param inventoryData
     * @param inMemoryData
     * @throws HmsException
     */
    public static void mergeSwitchNodeData( SwitchNode inventoryData, SwitchNode inMemoryData )
        throws HmsException
    {
        if ( inventoryData != null && inMemoryData == null )
        {
            logger.debug( "Creating the HMS Inventory switch data in InventoryLoader" );
            InventoryLoader.getInstance().getSwitchNodeMap().put( inventoryData.getSwitchId(), inventoryData );
        }
    }

}
