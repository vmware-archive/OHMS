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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static Logger logger = LoggerFactory.getLogger( ServerInfoHelperUtil.class );

    /**
     * Method to convert the Server Node to Server Info object
     *
     * @param serverNode
     * @return ServerInfo
     */
    public static ServerInfo convertServerNodeToServerInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();

        try
        {
            serverInfo = serverNode.getServerInfo( serverNode );
            serverInfo.setFruId( FruIdGeneratorUtil.generateFruIdHashCode( serverInfo.getComponentIdentifier(),
                                                                           serverInfo.getLocation() ) );
            serverInfo.setCpuInfo( convertToFruCpuInfo( serverNode, serverInfo.getFruId() ) );
            serverInfo.setStorageInfo( convertFruStorageInfo( serverNode, serverInfo.getFruId() ) );
            serverInfo.setStorageController( convertToFruStorageControllerInfo( serverNode, serverInfo.getFruId() ) );
            serverInfo.setMemoryInfo( convertToFruMemoryInfo( serverNode, serverInfo.getFruId() ) );
            serverInfo.setEthernetControllerList( convertToFruNICInfo( serverNode, serverInfo.getFruId() ) );

            return serverInfo;
        }
        catch ( Exception e )
        {
            logger.error( "Error while converting the ServerNode to ServerInfo object for node {} & exception: {} ",
                          serverNode.getNodeID(), e );
        }
        return null;
    }

    /**
     * Method to convert the Server Node to Server Info object with cpu, storage, memory zero at Aggregator Bootup. The
     * Inventory loader or hms inventory file data which has cpu, storage, memory resources zero.
     *
     * @param serverNode
     * @return ServerInfo
     */
    public static ServerInfo convertToServerInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();

        try
        {
            serverInfo = serverNode.getServerInfo( serverNode );
            serverInfo.setFruId( FruIdGeneratorUtil.generateFruIdHashCode( serverInfo.getComponentIdentifier(),
                                                                           serverInfo.getLocation() ) );
            serverInfo.setCpuInfo( new ArrayList<CpuInfo>() );
            serverInfo.setStorageInfo( new ArrayList<StorageInfo>() );
            serverInfo.setStorageController( new ArrayList<StorageController>() );
            serverInfo.setMemoryInfo( new ArrayList<MemoryInfo>() );
            serverInfo.setEthernetControllerList( new ArrayList<EthernetController>() );

            return serverInfo;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting the ServerNode to ServerInfo object for node: {} & exception: {}",
                          serverNode.getNodeID(), e );
        }
        return null;
    }

    /**
     * Convert the serverNode node CPUInfo to ServerInfo CpuInfo
     * 
     * @param serverNode
     * @return
     */
    public static List<CpuInfo> convertToFruCpuInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();

        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            return convertToFruCpuInfo( serverNode, serverInfo.getFruId() );
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to CpuInfo object for node: {} & exception: {} ",
                          serverNode.getNodeID(), e );
        }
        return new ArrayList<CpuInfo>();
    }

    /**
     * Convert the serverNode node CPUInfo to ServerInfo CpuInfo
     *
     * @param serverNode
     * @param serverFruID
     * @return
     */
    public static List<CpuInfo> convertToFruCpuInfo( ServerNode serverNode, String serverFruID )
    {
        List<CpuInfo> cpuInfoList = new ArrayList<CpuInfo>();

        try
        {
            if ( serverNode.getCpuInfo() != null && serverNode.getCpuInfo().size() > 0 )
            {
                int cpuArraySize = serverNode.getCpuInfo().size();
                for ( int i = 0; i < cpuArraySize; i++ )
                {
                    String fruId;
                    CpuInfo cpuInfo = new CpuInfo();
                    cpuInfo = cpuInfo.getCpuInfo( serverNode.getCpuInfo().get( i ), serverNode.getNodeID() );
                    fruId = FruIdGeneratorUtil.generateFruIdHashCode( cpuInfo.getComponentIdentifier(),
                                                                      cpuInfo.getLocation() );
                    cpuInfo.setFruId( FruIdGeneratorUtil.generateFruIdHashCodeServerComponent( fruId, serverFruID ) );
                    cpuInfoList.add( cpuInfo );
                }
            }
            return cpuInfoList;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to CpuInfo object for node: {} & exception: {} ",
                          serverNode.getNodeID(), e );
        }
        return cpuInfoList;
    }

    /**
     * Convert the serverNode storage Info to ServerInfo storage Info
     *
     * @param serverNode
     * @return
     */
    public static List<StorageInfo> convertFruStorageInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();

        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            return convertFruStorageInfo( serverNode, serverInfo.getFruId() );
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to storage Info object for node: {} & exception: {}",
                          serverNode.getNodeID(), e );
        }
        return new ArrayList<StorageInfo>();
    }

    /**
     * Convert the serverNode storage Info to ServerInfo storage Info
     *
     * @param serverNode
     * @return
     */
    public static List<StorageInfo> convertFruStorageInfo( ServerNode serverNode, String serverFruID )
    {
        List<StorageInfo> storageInfoList = new ArrayList<StorageInfo>();
        try
        {
            if ( serverNode.getHddInfo() != null && serverNode.getHddInfo().size() > 0 )
            {
                int hddArraySize = serverNode.getHddInfo().size();
                for ( int i = 0; i < hddArraySize; i++ )
                {
                    String fruId;
                    StorageInfo storageInfo = new StorageInfo();
                    storageInfo =
                        storageInfo.getStorageInfo( serverNode.getHddInfo().get( i ), serverNode.getNodeID() );
                    fruId = FruIdGeneratorUtil.generateFruIdHashCode( storageInfo.getComponentIdentifier(),
                                                                      storageInfo.getLocation() );
                    storageInfo.setFruId( FruIdGeneratorUtil.generateFruIdHashCodeServerComponent( fruId, serverFruID,
                                                                                                   storageInfo.getDiskType() ) );

                    storageInfoList.add( storageInfo );
                }
            }
            return storageInfoList;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to storage Info object for node: {} & exception: {} ",
                          serverNode.getNodeID(), e );
        }
        return storageInfoList;
    }

    /**
     * Convert the serverNode Memory Info to ServerInfo Memory Info
     *
     * @param serverNode
     * @return
     */
    public static List<MemoryInfo> convertToFruMemoryInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();

        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            return convertToFruMemoryInfo( serverNode, serverInfo.getFruId() );
        }
        catch ( Exception e )
        {
            logger.error( "Error while converting to Memory Info object for node: {}  & exception:  {} ",
                          serverNode.getNodeID(), e );
        }
        return new ArrayList<MemoryInfo>();
    }

    /**
     * Convert the serverNode Memory Info to ServerInfo Memory Info
     *
     * @param serverNode
     * @param serverFruID
     * @return
     */
    public static List<MemoryInfo> convertToFruMemoryInfo( ServerNode serverNode, String serverFruID )
    {
        List<MemoryInfo> memoryInfoList = new ArrayList<MemoryInfo>();

        try
        {
            if ( serverNode.getPhysicalMemoryInfo() != null && serverNode.getPhysicalMemoryInfo().size() > 0 )
            {
                int memArraySize = serverNode.getPhysicalMemoryInfo().size();
                for ( int i = 0; i < memArraySize; i++ )
                {
                    String fruId;
                    MemoryInfo memoryInfo = new MemoryInfo();
                    memoryInfo =
                        memoryInfo.getMemoryInfo( serverNode.getPhysicalMemoryInfo().get( i ), serverNode.getNodeID() );
                    fruId = FruIdGeneratorUtil.generateFruIdHashCode( memoryInfo.getComponentIdentifier(),
                                                                      memoryInfo.getLocation() );
                    memoryInfo.setFruId( FruIdGeneratorUtil.generateFruIdHashCodeServerComponent( fruId,
                                                                                                  serverFruID ) );

                    memoryInfoList.add( memoryInfo );
                }
            }
            return memoryInfoList;
        }
        catch ( Exception e )
        {
            logger.error( " Error while converting to Memory Info object for node: {} & exception: {}",
                          serverNode.getNodeID(), e );
        }
        return memoryInfoList;
    }

    /**
     * Convert the serverNode NIC Info to ServerInfo NIC Info
     *
     * @param serverNode
     * @return
     */
    public static List<EthernetController> convertToFruNICInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();

        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            return convertToFruNICInfo( serverNode, serverInfo.getFruId() );
        }
        catch ( Exception e )
        {
            logger.error( "Error while converting to Ethernet Controller Info object for node: {} & exception: {} ",
                          serverNode.getNodeID(), e );
        }
        return new ArrayList<EthernetController>();
    }

    /**
     * Convert the serverNode NIC Info to ServerInfo NIC Info
     *
     * @param serverNode
     * @param serverFruID
     * @return
     */
    public static List<EthernetController> convertToFruNICInfo( ServerNode serverNode, String serverFruID )
    {
        List<EthernetController> ethernetControllerList = new ArrayList<EthernetController>();

        try
        {
            if ( serverNode.getEthernetControllerList() != null && serverNode.getEthernetControllerList().size() > 0 )
            {
                int ethernetControllerArraySize = serverNode.getEthernetControllerList().size();
                for ( int i = 0; i < ethernetControllerArraySize; i++ )
                {
                    String fruId;
                    EthernetController ethernetController = new EthernetController();
                    ethernetController =
                        ethernetController.getEthernetController( serverNode.getEthernetControllerList().get( i ),
                                                                  serverNode.getNodeID() );
                    fruId = FruIdGeneratorUtil.generateFruIdHashCode( ethernetController.getComponentIdentifier(),
                                                                      ethernetController.getLocation() );
                    ethernetController.setFruId( FruIdGeneratorUtil.generateFruIdHashCodeServerComponent( fruId,
                                                                                                          serverFruID,
                                                                                                          ethernetController.getPortInfos().get( 0 ).getMacAddress() ) );

                    ethernetControllerList.add( ethernetController );
                }
            }
            return ethernetControllerList;
        }
        catch ( Exception e )
        {
            logger.error( "Error while converting to Ethernet Controller Info object for node {} & exception: {} ",
                          serverNode.getNodeID(), e );
        }
        return ethernetControllerList;
    }

    /**
     * Convert the serverNode Storage controller Info to ServerInfo Storage controller Info
     *
     * @param serverNode
     * @return
     */
    public static List<StorageController> convertToFruStorageControllerInfo( ServerNode serverNode )
    {
        ServerInfo serverInfo = new ServerInfo();

        try
        {
            serverInfo.setFruId( getServerNodeFruId( serverNode ) );
            return convertToFruStorageControllerInfo( serverNode, serverInfo.getFruId() );
        }
        catch ( Exception e )
        {
            logger.error( "Error while converting to Storage Controller Info object for node: {} & exception: {} ",
                          serverNode.getNodeID(), e );
        }
        return new ArrayList<StorageController>();
    }

    /**
     * Convert the serverNode Storage controller Info to ServerInfo Storage controller Info
     *
     * @param serverNode
     * @param serverFruID
     * @return
     */
    public static List<StorageController> convertToFruStorageControllerInfo( ServerNode serverNode, String serverFruID )
    {
        List<StorageController> storageControllerList = new ArrayList<StorageController>();

        try
        {
            if ( serverNode.getStorageControllerInfo() != null && serverNode.getStorageControllerInfo().size() > 0 )
            {
                String fruId;
                int storageControllerArraySize = serverNode.getStorageControllerInfo().size();
                for ( int i = 0; i < storageControllerArraySize; i++ )
                {
                    StorageController storageController = new StorageController();
                    storageController =
                        storageController.getStorageController( serverNode.getStorageControllerInfo().get( i ),
                                                                serverNode.getNodeID() );
                    fruId = FruIdGeneratorUtil.generateFruIdHashCode( storageController.getComponentIdentifier(),
                                                                      storageController.getLocation() );
                    storageController.setFruId( FruIdGeneratorUtil.generateFruIdHashCodeServerComponent( fruId,
                                                                                                         serverFruID,
                                                                                                         storageController.getDeviceName() ) );

                    storageControllerList.add( storageController );
                }
            }
            return storageControllerList;
        }
        catch ( Exception e )
        {
            logger.error( "Error while converting to Storage Controller Info object for node: {} & exception: {} ",
                          serverNode.getNodeID(), e );
        }
        return storageControllerList;
    }

    /*
     * Helps to get FRU ID for the Node
     */
    public static String getServerNodeFruId( ServerNode serverNode )
    {
        try
        {
            String FruId;
            ServerInfo serverInfo = new ServerInfo();
            ComponentIdentifier identifier = new ComponentIdentifier();

            // If Serial number and part number of the server is null, get it from the OOB to generate the
            // Server FRU ID
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
            FruId = FruIdGeneratorUtil.generateFruIdHashCode( serverInfo.getComponentIdentifier(),
                                                              serverInfo.getLocation() );
            return FruId;
        }
        catch ( Exception e )
        {
            logger.error( "Error while getting the FRU ID for the server node: {}  & exception: {}",
                          serverNode.getNodeID(), e );
        }
        return null;
    }

    /**
     * Updating ServerInfo From Inventory loader Data. We take updated data form the Inventory loader (which gets update
     * during /refreshinventory request) and check for the null condition and then update ServerInfo with Inventory
     * loader Data.
     *
     * @param serverInfo
     * @param serverNode
     * @return ServerInfo
     */
    public static ServerInfo updateServerInfoData( ServerInfo serverInfo, ServerNode serverNode )
    {
        if ( serverInfo == null || serverNode == null )
        {
            // which is null.
            return serverInfo;
        }
        if ( serverNode.getManagementIp() != null )
        {
            serverInfo.setManagementIpAddress( serverNode.getManagementIp() );
        }
        if ( serverNode.getIbIpAddress() != null )
        {
            serverInfo.setInBandIpAddress( serverNode.getIbIpAddress() );
        }
        if ( serverNode.getHypervisorName() != null )
        {
            serverInfo.setOsName( serverNode.getHypervisorName() );
        }
        if ( serverNode.getHypervisorProvider() != null )
        {
            serverInfo.setOsVendor( serverNode.getHypervisorProvider() );
        }
        if ( serverNode.getLocation() != null )
        {
            serverInfo.setLocation( serverNode.getLocation() );
        }
        if ( serverNode.getAdminStatus() != null )
        {
            serverInfo.setAdminStatus( serverNode.getAdminStatus().toString() );
        }
        if ( serverNode.getBoardVendor() != null )
        {
            serverInfo.getComponentIdentifier().setManufacturer( serverNode.getBoardVendor() );
        }
        if ( serverNode.getBoardProductName() != null )
        {
            serverInfo.getComponentIdentifier().setProduct( serverNode.getBoardProductName() );
        }
        if ( serverNode.getBoardPartNumber() != null )
        {
            serverInfo.getComponentIdentifier().setPartNumber( serverNode.getBoardPartNumber() );
        }
        if ( serverNode.getBoardSerialNumber() != null )
        {
            serverInfo.getComponentIdentifier().setSerialNumber( serverNode.getBoardSerialNumber() );
        }
        if ( serverNode.getBoardMfgDate() != null )
        {
            serverInfo.getComponentIdentifier().setManufacturingDate( serverNode.getBoardMfgDate() );
        }
        return serverInfo;
    }
}