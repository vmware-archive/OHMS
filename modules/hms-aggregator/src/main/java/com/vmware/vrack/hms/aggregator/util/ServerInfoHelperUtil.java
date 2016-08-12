/* ********************************************************************************
 * ServerInfoHelperUtil.java
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

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.aggregator.HostDataAggregator;
import com.vmware.vrack.hms.common.rest.model.CpuInfo;
import com.vmware.vrack.hms.common.rest.model.EthernetController;
import com.vmware.vrack.hms.common.rest.model.MemoryInfo;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.StorageController;
import com.vmware.vrack.hms.common.rest.model.StorageInfo;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.util.FruIdGeneratorUtil;
import com.vmware.vrack.hms.inventory.InventoryLoader;

/*
* Helper class to convert the Server Node object to the Server Info object as per the FRU Model
* @author VMware Inc.
*/
public class ServerInfoHelperUtil
{
    private static Logger logger = Logger.getLogger( ServerInfoHelperUtil.class );

    /**
     * Method to convert the Server Node to Server Info object
     *
     * @param serverNode
     * @return ServerInfo
     */
    public ServerInfo convertServerNodeToServerInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();
        FruIdGeneratorUtil fruIdGeneratorUtil = new FruIdGeneratorUtil();
        try
        {
            serverInfo = serverNode.getServerInfo( serverNode );
            serverInfo.setFruId( String.valueOf( fruIdGeneratorUtil.generateFruIdHashCode( serverInfo.getComponentIdentifier(),
                                                                                           serverInfo.getLocation() ) ) );
            serverInfo.setCpuInfo( convertToFruCpuInfo( serverNode ) );
            serverInfo.setStorageInfo( convertFruStorageInfo( serverNode ) );
            serverInfo.setStorageController( convertToFruStorageControllerInfo( serverNode ) );
            serverInfo.setMemoryInfo( convertToFruMemoryInfo( serverNode ) );
            serverInfo.setEthernetControllerList( convertToFruNICInfo( serverNode ) );
            return serverInfo;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting the ServerNode to ServerInfo object for node "
                + serverNode.getNodeID(), e );
        }
        return null;
    }

    public List<CpuInfo> convertToFruCpuInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();
        FruIdGeneratorUtil fruIdGeneratorUtil = new FruIdGeneratorUtil();
        List<CpuInfo> cpuInfoList = new ArrayList<CpuInfo>();
        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            if ( serverNode.getCpuInfo() != null )
            {
                int cpuArraySize = serverNode.getCpuInfo().size();
                for ( int i = 0; i < cpuArraySize; i++ )
                {
                    CpuInfo cpuInfo = new CpuInfo();
                    cpuInfo = cpuInfo.getCpuInfo( serverNode.getCpuInfo().get( i ), serverNode.getNodeID() );
                    cpuInfo.setFruId( ( String.valueOf( fruIdGeneratorUtil.generateFruIdHashCode( cpuInfo.getComponentIdentifier(),
                                                                                                  cpuInfo.getLocation() ) ) )
                        + serverInfo.getFruId() );
                    cpuInfoList.add( cpuInfo );
                }
            }
            return cpuInfoList;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to CpuInfo object for node " + serverNode.getNodeID(), e );
        }
        return null;
    }

    public List<StorageInfo> convertFruStorageInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();
        List<StorageInfo> storageInfoList = new ArrayList<StorageInfo>();
        FruIdGeneratorUtil fruIdGeneratorUtil = new FruIdGeneratorUtil();
        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            if ( serverNode.getHddInfo() != null )
            {
                int hddArraySize = serverNode.getHddInfo().size();
                Long fruId;
                for ( int i = 0; i < hddArraySize; i++ )
                {
                    StorageInfo storageInfo = new StorageInfo();
                    storageInfo =
                        storageInfo.getStorageInfo( serverNode.getHddInfo().get( i ), serverNode.getNodeID() );
                    fruId = fruIdGeneratorUtil.generateFruIdHashCode( storageInfo.getComponentIdentifier(),
                                                                      storageInfo.getLocation() );
                    storageInfo.setFruId( fruIdGeneratorUtil.generateFruIdHashCodeStorage( fruId, serverInfo.getFruId(),
                                                                                           storageInfo.getDiskType() ) );
                    storageInfoList.add( storageInfo );
                }
            }
            return storageInfoList;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to storage Info object for node " + serverNode.getNodeID(), e );
        }
        return null;
    }

    public List<MemoryInfo> convertToFruMemoryInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();
        List<MemoryInfo> memoryInfoList = new ArrayList<MemoryInfo>();
        FruIdGeneratorUtil fruIdGeneratorUtil = new FruIdGeneratorUtil();
        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            if ( serverNode.getPhysicalMemoryInfo() != null )
            {
                int memArraySize = serverNode.getPhysicalMemoryInfo().size();
                for ( int i = 0; i < memArraySize; i++ )
                {
                    MemoryInfo memoryInfo = new MemoryInfo();
                    memoryInfo =
                        memoryInfo.getMemoryInfo( serverNode.getPhysicalMemoryInfo().get( i ), serverNode.getNodeID() );
                    memoryInfo.setFruId( ( String.valueOf( fruIdGeneratorUtil.generateFruIdHashCode( memoryInfo.getComponentIdentifier(),
                                                                                                     memoryInfo.getLocation() ) ) )
                        + serverInfo.getFruId() );
                    memoryInfoList.add( memoryInfo );
                }
            }
            return memoryInfoList;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to Memory Info object for node " + serverNode.getNodeID(), e );
        }
        return null;
    }

    public List<EthernetController> convertToFruNICInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();
        List<EthernetController> ethernetControllerList = new ArrayList<EthernetController>();
        FruIdGeneratorUtil fruIdGeneratorUtil = new FruIdGeneratorUtil();
        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            if ( serverNode.getEthernetControllerList() != null )
            {
                int ethernetControllerArraySize = serverNode.getEthernetControllerList().size();
                for ( int i = 0; i < ethernetControllerArraySize; i++ )
                {
                    Long fruId;
                    EthernetController ethernetController = new EthernetController();
                    ethernetController =
                        ethernetController.getEthernetController( serverNode.getEthernetControllerList().get( i ),
                                                                  serverNode.getNodeID() );
                    fruId = fruIdGeneratorUtil.generateFruIdHashCode( ethernetController.getComponentIdentifier(),
                                                                      ethernetController.getLocation() );
                    ethernetController.setFruId( fruIdGeneratorUtil.generateFruIdHashCodeEthernetController( fruId,
                                                                                                             ethernetController.getPortInfos().get( 0 ).getMacAddress(),
                                                                                                             serverInfo.getFruId() ) );
                    ethernetControllerList.add( ethernetController );
                }
            }
            return ethernetControllerList;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to Ethernet Controller Info object for node "
                + serverNode.getNodeID(), e );
        }
        return null;
    }

    public List<StorageController> convertToFruStorageControllerInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();
        List<StorageController> storageControllerList = new ArrayList<StorageController>();
        FruIdGeneratorUtil fruIdGeneratorUtil = new FruIdGeneratorUtil();
        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            if ( serverNode.getStorageControllerInfo() != null )
            {
                Long fruId;
                int storageControllerArraySize = serverNode.getStorageControllerInfo().size();
                for ( int i = 0; i < storageControllerArraySize; i++ )
                {
                    StorageController storageController = new StorageController();
                    storageController =
                        storageController.getStorageController( serverNode.getStorageControllerInfo().get( i ),
                                                                serverNode.getNodeID() );
                    fruId = fruIdGeneratorUtil.generateFruIdHashCode( storageController.getComponentIdentifier(),
                                                                      storageController.getLocation() );
                    storageController.setFruId( fruIdGeneratorUtil.generateFruIdHashCodeStorageController( fruId,
                                                                                                           storageController.getDeviceName(),
                                                                                                           serverInfo.getFruId() ) );
                    storageControllerList.add( storageController );
                }
            }
            return storageControllerList;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to Storage Controller Info object for node "
                + serverNode.getNodeID(), e );
        }
        return null;
    }

    /*
     * Helps to get FRU ID for the Node
     */
    public String getServerNodeFruId( ServerNode serverNode )
    {
        try
        {
            String FruId;
            ServerInfo serverInfo = new ServerInfo();
            ComponentIdentifier identifier = new ComponentIdentifier();
            FruIdGeneratorUtil fruIdGeneratorUtil = new FruIdGeneratorUtil();
            // If Serial number and part number of the server is null, get it from the OOB to generate the Server FRU ID
            if ( serverNode.getBoardSerialNumber() == null || serverNode.getBoardPartNumber() == null )
            {
                HostDataAggregator aggregator = new HostDataAggregator();
                ServerNode node = aggregator.getServerNodeOOBData( serverNode.getNodeID() );
                InventoryLoader.getInstance().getNodeMap().get( serverNode.getNodeID() ).setBoardSerialNumber( node.getBoardSerialNumber() );
                InventoryLoader.getInstance().getNodeMap().get( serverNode.getNodeID() ).setBoardPartNumber( node.getBoardPartNumber() );
            }
            identifier.setManufacturer( serverNode.getBoardVendor() );
            identifier.setProduct( serverNode.getBoardProductName() );
            identifier.setSerialNumber( serverNode.getBoardSerialNumber() );
            identifier.setPartNumber( serverNode.getBoardPartNumber() );
            serverInfo.setComponentIdentifier( identifier );
            serverInfo.setLocation( serverNode.getLocation() );
            FruId = String.valueOf( fruIdGeneratorUtil.generateFruIdHashCode( serverInfo.getComponentIdentifier(),
                                                                              serverInfo.getLocation() ) );
            return FruId;
        }
        catch ( Exception e )
        {
            logger.error( " Error while getting the FRU ID for the server node" + serverNode.getNodeID(), e );
        }
        return null;
    }
}
