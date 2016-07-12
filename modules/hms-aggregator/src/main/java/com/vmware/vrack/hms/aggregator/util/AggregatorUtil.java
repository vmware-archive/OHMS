/* ********************************************************************************
 * AggregatorUtil.java
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

import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.boardvendorservice.api.ib.IInbandService;
import com.vmware.vrack.hms.common.boardvendorservice.resource.ServiceHmsNode;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.bios.BiosInfo;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.common.util.Constants;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

/**
 * @author sgakhar Provides utility functions to be used while aggregating OOB and IB data
 */
public class AggregatorUtil
{
    private static Logger logger = Logger.getLogger( AggregatorUtil.class );

    /**
     * Verifies if the server component is available OOB
     *
     * @param node
     * @param component
     * @return
     */
    public static boolean isComponentAvilableOOB( ServerNode node, ServerComponent component )
    {
        if ( component.getComponentInfoAPI() != null )
            InventoryLoader.getInstance().isServerComponentAvailableOOB( node.getNodeID(),
                                                                         component.getComponentInfoAPI() );
        return false;
    }

    /**
     * Returns InbandService instance for the specified node
     *
     * @param node
     * @return
     * @throws HmsException
     */
    private static IInbandService getInBandService( ServerNode node )
        throws HmsException
    {
        IInbandService service = InBandServiceProvider.getBoardService( node.getServiceObject() );
        return service;
    }

    private static void agregateNodeInBnadBasicInfo( ServerNode node, ServerNode inBandNode )
    {
        try
        {
            MergeDataUtil.mergeInbandData( node, inBandNode, false );
        }
        catch ( HmsException e )
        {
            logger.error( "error while aggregating inband node info(ip, user, password) in oob server node object", e );
        }
    }

    /**
     * Sets Server Component Info got using InBand Service in node object
     *
     * @param node
     * @param component
     * @throws HmsException
     */
    public static void getServerComponentIB( ServerNode node, ServerComponent component )
        throws HmsException
    {
        IInbandService service = getInBandService( node );
        ServerNode inBandNode = InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() );
        ServiceHmsNode serviceNode = inBandNode.getServiceObject();
        agregateNodeInBnadBasicInfo( node, inBandNode );
        switch ( component )
        {
            case CPU:
                List<CPUInfo> cpuInfo = null;
                cpuInfo = service.getCpuInfo( serviceNode );
                node.setCpuInfo( cpuInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setCpuInfo( cpuInfo );
                break;
            case STORAGE:
                List<HddInfo> hddInfo = null;
                hddInfo = service.getHddInfo( serviceNode );
                node.setHddInfo( hddInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setHddInfo( hddInfo );
                break;
            case STORAGE_CONTROLLER:
                List<StorageControllerInfo> storageControllerInfo = null;
                storageControllerInfo = service.getStorageControllerInfo( serviceNode );
                node.setStorageControllerInfo( storageControllerInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setStorageControllerInfo( storageControllerInfo );
                break;
            case MEMORY:
                List<PhysicalMemory> memoryInfo = null;
                memoryInfo = service.getSystemMemoryInfo( serviceNode );
                node.setPhysicalMemoryInfo( memoryInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setPhysicalMemoryInfo( memoryInfo );
                break;
            case NIC:
                List<EthernetController> nicInfo = null;
                nicInfo = service.getNicInfo( serviceNode );
                /*
                 * Need to be reviewed while reviewing topology code. //TODO: get Additional NIC info of Switch name ,
                 * Port and Mac Details from Switch topology try { NicDataUtil.getAdditionalNicInfo(nicInfo,
                 * node.getNodeID()); } catch(Exception e) { logger.error(
                 * "Exception while getting extra information for nic via NetTopologyElements: "); }
                 */
                node.setEthernetControllerList( nicInfo );
                InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setEthernetControllerList( nicInfo );
                break;
            case BIOS:
                BiosInfo biosInfo = null;
                biosInfo = service.getBiosInfo( serviceNode );
                if ( biosInfo != null )
                {
                    node.setBiosVersion( biosInfo.getBiosVersion() );
                    node.setBiosReleaseDate( biosInfo.getBiosReleaseDate() );
                    InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setBiosVersion( biosInfo.getBiosVersion() );
                    InventoryLoader.getInstance().getNodeMap().get( node.getNodeID() ).setBiosReleaseDate( biosInfo.getBiosReleaseDate() );
                }
                break;
        }
    }

    /**
     * Sets Server Component Info got using OOB Service in node object Gets Server Component using InBand Service
     *
     * @param node
     * @param component
     * @throws HmsException
     */
    public static void getServerComponentOOB( ServerNode node, ServerComponent component )
        throws HmsException
    {
        String path;
        switch ( component )
        {
            case CPU:
                List<CPUInfo> cpuInfo = null;
                path = Constants.HMS_OOB_CPU_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                cpuInfo = MonitoringUtil.<CPUInfo>getServerComponentOOB( path );
                node.setCpuInfo( cpuInfo );
                break;
            case STORAGE:
                List<HddInfo> hddInfo = null;
                path = Constants.HMS_OOB_HDD_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                hddInfo = MonitoringUtil.<HddInfo>getServerComponentOOB( path );
                node.setHddInfo( hddInfo );
                break;
            case MEMORY:
                List<PhysicalMemory> memoryInfo = null;
                path = Constants.HMS_OOB_MEMORY_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                memoryInfo = MonitoringUtil.<PhysicalMemory>getServerComponentOOB( path );
                node.setPhysicalMemoryInfo( memoryInfo );
                break;
            case NIC:
                List<EthernetController> nicInfo = null;
                path = Constants.HMS_OOB_NIC_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                nicInfo = MonitoringUtil.<EthernetController>getServerComponentOOB( path );
                node.setEthernetControllerList( nicInfo );
                break;
            case STORAGE_CONTROLLER:
                List<StorageControllerInfo> storageControllerInfo = null;
                path = Constants.HMS_OOB_STORAGE_CONTROLLER_INFO_ENDPOINT.replace( "{host_id}", node.getNodeID() );
                storageControllerInfo = MonitoringUtil.<StorageControllerInfo>getServerComponentOOB( path );
                node.setStorageControllerInfo( storageControllerInfo );
                break;
        }
    }
}
