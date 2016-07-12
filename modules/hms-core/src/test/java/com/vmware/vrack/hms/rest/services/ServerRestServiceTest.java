/* ********************************************************************************
 * ServerRestServiceTest.java
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
package com.vmware.vrack.hms.rest.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.boardservice.HmsPluginServiceCallWrapper;
import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.BaseResponse;
import com.vmware.vrack.hms.common.resource.AcpiPowerState;
import com.vmware.vrack.hms.common.resource.BmcUser;
import com.vmware.vrack.hms.common.resource.SelfTestResults;
import com.vmware.vrack.hms.common.resource.SystemBootOptions;
import com.vmware.vrack.hms.common.resource.chassis.BiosBootType;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceSelector;
import com.vmware.vrack.hms.common.resource.chassis.BootDeviceType;
import com.vmware.vrack.hms.common.resource.chassis.BootOptionsValidity;
import com.vmware.vrack.hms.common.resource.chassis.ChassisIdentifyOptions;
import com.vmware.vrack.hms.common.resource.fru.EthernetController;
import com.vmware.vrack.hms.common.resource.sel.SelFetchDirection;
import com.vmware.vrack.hms.common.resource.sel.SelInfo;
import com.vmware.vrack.hms.common.resource.sel.SelOption;
import com.vmware.vrack.hms.common.resource.sel.SelTask;
import com.vmware.vrack.hms.common.servernodes.api.HmsApi;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.servernodes.api.ServerNodePowerStatus;
import com.vmware.vrack.hms.common.servernodes.api.cpu.CPUInfo;
import com.vmware.vrack.hms.common.servernodes.api.hdd.HddInfo;
import com.vmware.vrack.hms.common.servernodes.api.memory.PhysicalMemory;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;
import com.vmware.vrack.hms.node.server.ServerNodeConnector;
import com.vmware.vrack.hms.testplugin.BoardService_TEST;

public class ServerRestServiceTest
{
    private static Logger logger = Logger.getLogger( ServerRestServiceTest.class );

    /**
     * Insert test node in NodeMap
     * 
     * @param node
     */
    public static void insertNodeInNodeMap( ServerNode node )
    {
        ServerNodeConnector.getInstance().nodeMap.put( "N1", node );
    }

    /**
     * Insert test board service for test node
     * 
     * @throws Exception
     */
    public static void addBoardServiceForNode()
        throws Exception
    {
        ServerNode node = (ServerNode) ServerNodeConnector.getInstance().nodeMap.get( "N1" );
        BoardService_TEST boardService_TEST = new BoardService_TEST();
        try
        {
            BoardServiceProvider.addBoardServiceClass( node.getServiceObject(), BoardService_TEST.class, true );
            HmsPluginServiceCallWrapper.addNodeRateLimitModelForNode( "N1" );
        }
        catch ( HmsException e )
        {
            logger.error( "Unable to add boardservice for node: " + node.getNodeID() );
        }
    }

    /**
     * Remove test node from NodeMap
     */
    public static void removeNodeFromNodeMap()
    {
        ServerNodeConnector.getInstance().nodeMap.remove( "N1" );
    }

    /**
     * Remove BoardService for test node
     */
    public static void removeBoardServiceForNode()
    {
        try
        {
            BoardServiceProvider.removeBoardServiceClass( getServerNode().getServiceObject() );
        }
        catch ( HmsException e )
        {
            logger.error( "Unable to clear boardservice for node: " + getServerNode().getNodeID() );
        }
    }

    @BeforeClass
    public static void clearNodeMapAndBoardService()
    {
        HmsConfigHolder.initializeHmsAppProperties();
        removeNodeFromNodeMap();
        removeBoardServiceForNode();
    }

    @AfterClass
    public static void cleanUp()
    {
        removeNodeFromNodeMap();
        removeBoardServiceForNode();
    }

    public static ServerNode getServerNode()
    {
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setBoardProductName( "S2600GZ" );
        node.setBoardVendor( "Intel" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setManagementIp( "10.28.197.208" );
        return node;
    }

    @Test( expected = HMSRestException.class )
    public void getCpuInfo_nodeNotInNodeMap()
        throws HMSRestException
    {
        clearNodeMapAndBoardService();
        ServerRestService restService = new ServerRestService();
        List<CPUInfo> cpuInfos = restService.getCpuInfo( "N1" );
        assertNotNull( cpuInfos );
    }

    @Test( expected = HMSRestException.class )
    public void getMemoryInfo_nodeNotInNodeMap()
        throws HMSRestException
    {
        clearNodeMapAndBoardService();
        ServerRestService restService = new ServerRestService();
        List<PhysicalMemory> memories = restService.getMemoryInfo( "N1" );
        assertNotNull( memories );
    }

    @Test( expected = HMSRestException.class )
    public void getHddInfo_nodeNotInNodeMap()
        throws HMSRestException
    {
        clearNodeMapAndBoardService();
        ServerRestService restService = new ServerRestService();
        List<HddInfo> hddInfos = restService.getHddInfo( "N1" );
        assertNotNull( hddInfos );
    }

    @Test( expected = HMSRestException.class )
    public void getNicInfo_nodeNotInNodeMap()
        throws HMSRestException
    {
        clearNodeMapAndBoardService();
        ServerRestService restService = new ServerRestService();
        List<EthernetController> ethernetControllers = restService.getNicInfo( "N1" );
        assertNotNull( ethernetControllers );
    }

    @Test( expected = HMSRestException.class )
    public void getStorageControllerInfo_nodeNotInNodeMap()
        throws HMSRestException
    {
        clearNodeMapAndBoardService();
        ServerRestService restService = new ServerRestService();
        List<StorageControllerInfo> storageControllerInfo = restService.getStorageControllerInfo( "N1" );
        assertNotNull( storageControllerInfo );
    }

    @Test
    public void getHostNode_nodeAvailable()
        throws HMSRestException
    {
        insertNodeInNodeMap( getServerNode() );
        ServerRestService restService = new ServerRestService();
        ServerNode returnedNode = restService.getHostNode( "N1" );
        assertNotNull( returnedNode );
        assertTrue( "Intel".equals( returnedNode.getBoardVendor() ) );
        assertTrue( "10.28.197.28".equals( returnedNode.getIbIpAddress() ) );
    }

    @Test( expected = HMSRestException.class )
    public void getCpuInfo_nodeInNodeMap_NoBoardService()
        throws HMSRestException
    {
        insertNodeInNodeMap( getServerNode() );
        removeBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<CPUInfo> cpuInfos = restService.getCpuInfo( "N1" );
        assertNotNull( cpuInfos );
        assertEquals( 2, cpuInfos.size() );
    }

    @Test( expected = HMSRestException.class )
    public void getMemoryInfo_nodeInNodeMap_NoBoardService()
        throws HMSRestException
    {
        insertNodeInNodeMap( getServerNode() );
        removeBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<PhysicalMemory> memories = restService.getMemoryInfo( "N1" );
        assertNotNull( memories );
        assertEquals( 2, memories.size() );
    }

    @Test( expected = HMSRestException.class )
    public void getHddInfo_nodeInNodeMap_NoBoardService()
        throws HMSRestException
    {
        insertNodeInNodeMap( getServerNode() );
        removeBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<HddInfo> hddInfos = restService.getHddInfo( "N1" );
        assertNotNull( hddInfos );
        assertEquals( 2, hddInfos.size() );
    }

    @Test( expected = HMSRestException.class )
    public void getNicInfo_nodeInNodeMap_NoBoardService()
        throws HMSRestException
    {
        insertNodeInNodeMap( getServerNode() );
        removeBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<EthernetController> ethernetControllers = restService.getNicInfo( "N1" );
        assertNotNull( ethernetControllers );
        assertEquals( 2, ethernetControllers.size() );
    }

    @Test( expected = HMSRestException.class )
    public void getStorageController_nodeInNodeMap_NoBoardService()
        throws HMSRestException
    {
        insertNodeInNodeMap( getServerNode() );
        removeBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<StorageControllerInfo> storageControllerInfo = restService.getStorageControllerInfo( "N1" );
        assertNotNull( storageControllerInfo );
        assertEquals( 2, storageControllerInfo.size() );
    }

    @Test
    public void getCpuInfo_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<CPUInfo> cpuInfos = restService.getCpuInfo( "N1" );
        assertNotNull( cpuInfos );
        assertEquals( 1, cpuInfos.size() );
        assertEquals( "CPU_0", cpuInfos.get( 0 ).getId() );
    }

    @Test
    public void getMemoryInfo_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<PhysicalMemory> memories = restService.getMemoryInfo( "N1" );
        assertNotNull( memories );
        assertEquals( 2, memories.size() );
        assertEquals( ServerComponent.MEMORY, memories.get( 0 ).getComponent() );
    }

    @Test
    public void getHddInfo_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<HddInfo> hddInfos = restService.getHddInfo( "N1" );
        assertNotNull( hddInfos );
        assertEquals( 2, hddInfos.size() );
        assertEquals( ServerComponent.STORAGE, hddInfos.get( 0 ).getComponent() );
    }

    @Test
    public void getStorageControllerInfo_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<StorageControllerInfo> storageControllerInfo = restService.getStorageControllerInfo( "N1" );
        assertNotNull( storageControllerInfo );
        assertEquals( 2, storageControllerInfo.size() );
        assertEquals( ServerComponent.STORAGE_CONTROLLER, storageControllerInfo.get( 0 ).getComponent() );
    }

    @Test
    public void getNicInfo_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<EthernetController> ethernetControllers = restService.getNicInfo( "N1" );
        assertNotNull( ethernetControllers );
        assertEquals( 1, ethernetControllers.size() );
        assertNotNull( ethernetControllers.get( 0 ).getPortInfos() );
        assertTrue( ethernetControllers.get( 0 ).getPortInfos().size() > 0 );
        assertEquals( ServerComponent.NIC, ethernetControllers.get( 0 ).getComponent() );
    }

    @Test
    public void getHostPowerStatus_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        ServerNodePowerStatus status = restService.getHostPowerStatus( "N1" );
        assertNotNull( status );
        assertNotNull( status.isPowered() );
        assertNotNull( status.isDiscoverable() );
        assertTrue( status.isPowered() );
    }

    @Test
    public void getHostSelfTestResults_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        SelfTestResults selfTestResults = restService.getHostSelfTestResults( "N1" );
        assertNotNull( selfTestResults );
        assertNotNull( selfTestResults.getSelfTestResult() );
        assertNotNull( selfTestResults.getSelfTestResultCode() );
    }

    @Test
    public void getBmcUsers_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<BmcUser> bmcUsers = restService.getBmcUsers( "N1" );
        assertNotNull( bmcUsers );
        assertEquals( 2, bmcUsers.size() );
        assertNotNull( bmcUsers.get( 0 ).getUserName() );
        assertTrue( bmcUsers.get( 0 ).getUserName().contains( "test" ) );
    }

    @Test
    public void getHostAcpiPowerState_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        AcpiPowerState acpiPowerState = restService.getHostAcpiPowerState( "N1" );
        assertNotNull( acpiPowerState );
        assertEquals( "D0", acpiPowerState.getDeviceAcpiPowerState() );
        assertEquals( "S0", acpiPowerState.getSystemAcpiPowerState() );
    }

    @Test
    public void getSystemBootOptions_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        SystemBootOptions bootOptions = restService.getSystemBootOptions( "N1" );
        assertNotNull( bootOptions );
        assertEquals( BootOptionsValidity.Persistent, bootOptions.getBootOptionsValidity() );
        assertEquals( BootDeviceSelector.PXE, bootOptions.getBootDeviceSelector() );
        assertEquals( BootDeviceType.Internal, bootOptions.getBootDeviceType() );
        assertEquals( BiosBootType.Legacy, bootOptions.getBiosBootType() );
    }

    @Test( expected = HMSRestException.class )
    public void setSystemBootOptions_validNode_noBoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        SystemBootOptions systemBootOptions = new SystemBootOptions();
        systemBootOptions.setBiosBootType( BiosBootType.Legacy );
        systemBootOptions.setBootDeviceInstanceNumber( 0 );
        systemBootOptions.setBootDeviceSelector( BootDeviceSelector.PXE );
        systemBootOptions.setBootDeviceType( BootDeviceType.Internal );
        systemBootOptions.setBootOptionsValidity( BootOptionsValidity.Persistent );
        BaseResponse response = restService.getSystemBootOptions( "N2", systemBootOptions );
        assertNotNull( response );
        assertTrue( 200 == response.getStatusCode() );
    }

    @Test
    public void setSystemBootOptions_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        SystemBootOptions systemBootOptions = new SystemBootOptions();
        systemBootOptions.setBiosBootType( BiosBootType.Legacy );
        systemBootOptions.setBootDeviceInstanceNumber( 0 );
        systemBootOptions.setBootDeviceSelector( BootDeviceSelector.PXE );
        systemBootOptions.setBootDeviceType( BootDeviceType.Internal );
        systemBootOptions.setBootOptionsValidity( BootOptionsValidity.Persistent );
        BaseResponse response = restService.getSystemBootOptions( "N1", systemBootOptions );
        assertNotNull( response );
        assertTrue( 202 == response.getStatusCode() );
    }

    @Test( expected = HMSRestException.class )
    public void chassisIdentify_nodeValid_noBoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        ChassisIdentifyOptions chassisIdentifyOptions = new ChassisIdentifyOptions();
        chassisIdentifyOptions.setForceIdentifyChassis( false );
        chassisIdentifyOptions.setIdentify( true );
        chassisIdentifyOptions.setInterval( 15 );
        BaseResponse response = restService.chassisIdentify( "N2", chassisIdentifyOptions );
        assertNotNull( response );
        assertTrue( 202 == response.getStatusCode() );
    }

    @Test
    public void chassisIdentify_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        ChassisIdentifyOptions chassisIdentifyOptions = new ChassisIdentifyOptions();
        chassisIdentifyOptions.setForceIdentifyChassis( false );
        chassisIdentifyOptions.setIdentify( true );
        chassisIdentifyOptions.setInterval( 15 );
        BaseResponse response = restService.chassisIdentify( "N1", chassisIdentifyOptions );
        assertNotNull( response );
        assertTrue( 202 == response.getStatusCode() );
    }

    @Test( expected = HMSRestException.class )
    public void selInfo_nodeValid_noBoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        SelOption selOption = new SelOption();
        selOption.setDirection( SelFetchDirection.RecentEntries );
        selOption.setRecordCount( 64 );
        selOption.setSelTask( SelTask.SelDetails );
        SelInfo selInfo = restService.selInfo( "N2", selOption );
        assertNotNull( selInfo );
    }

    @Test
    public void selInfo_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        SelOption selOption = new SelOption();
        selOption.setDirection( SelFetchDirection.RecentEntries );
        selOption.setRecordCount( 64 );
        selOption.setSelTask( SelTask.SelDetails );
        SelInfo selInfo = restService.selInfo( "N1", selOption );
        assertNotNull( selInfo );
    }

    @Test( expected = HMSRestException.class )
    public void getAvailableNodeOperations_nodeValid_noBoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<HmsApi> hmsApis = restService.getAvailableNodeOperations( "N2" );
        assertNotNull( hmsApis );
        assertTrue( hmsApis.size() > 0 );
    }

    @Test
    public void getAvailableNodeOperations_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        List<HmsApi> hmsApis = restService.getAvailableNodeOperations( "N1" );
        assertNotNull( hmsApis );
        assertTrue( hmsApis.size() > 0 );
    }

    @Test( expected = HMSRestException.class )
    public void updateNodes_nodeValid_noBoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        BaseResponse response = restService.updateNodes( "N2", "power_up" );
        assertNotNull( response );
        assertTrue( 202 == response.getStatusCode() );
    }

    @Test
    public void updateNodes_nodeInNodeMap_BoardServiceFound()
        throws Exception
    {
        insertNodeInNodeMap( getServerNode() );
        addBoardServiceForNode();
        ServerRestService restService = new ServerRestService();
        BaseResponse response = restService.updateNodes( "N1", "power_up" );
        assertNotNull( response );
        assertTrue( 202 == response.getStatusCode() );
        response = restService.updateNodes( "N1", "cold_reset" );
        assertNotNull( response );
        assertTrue( 202 == response.getStatusCode() );
        response = restService.updateNodes( "N1", "hard_reset" );
        assertNotNull( response );
        assertTrue( 202 == response.getStatusCode() );
        response = restService.updateNodes( "N1", "power_down" );
        assertNotNull( response );
        assertTrue( 202 == response.getStatusCode() );
        response = restService.updateNodes( "N1", "power_cycle" );
        assertNotNull( response );
        assertTrue( 202 == response.getStatusCode() );
    }
}
