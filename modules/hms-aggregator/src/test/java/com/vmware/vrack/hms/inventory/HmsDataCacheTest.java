/* ********************************************************************************
 * HmsDataCacheTest.java
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vrack.hms.common.resource.fru.FruOperationalStatus;
import com.vmware.vrack.hms.common.rest.model.CpuInfo;
import com.vmware.vrack.hms.common.rest.model.FruComponent;
import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.StorageInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.ComponentIdentifier;
import com.vmware.vrack.hms.common.servernodes.api.ServerComponent;
import com.vmware.vrack.hms.common.servernodes.api.SwitchComponentEnum;
import com.vmware.vrack.hms.common.servernodes.api.storagecontroller.StorageControllerInfo;

/**
 * Unit test for the HMS Data Cache
 *
 * @author VMware Inc.
 */
public class HmsDataCacheTest
{

    ServerInfo serverInfo = new ServerInfo();

    NBSwitchInfo switchInfo = new NBSwitchInfo();

    List<CpuInfo> listCpu = new ArrayList<CpuInfo>();

    List<StorageInfo> listStorage = new ArrayList<StorageInfo>();

    List<StorageControllerInfo> storageControllerInfoList = new ArrayList<StorageControllerInfo>();

    @Before
    public void initialize()
    {
        CpuInfo cpu1 = new CpuInfo();
        ComponentIdentifier cpuIdentifier1 = new ComponentIdentifier();
        cpuIdentifier1.setManufacturer( "INTEL" );
        cpuIdentifier1.setProduct( "Intel Xeon Processor" );
        cpu1.setComponentIdentifier( cpuIdentifier1 );
        cpu1.setId( "1" );
        cpu1.setCpuFrequencyInHertz( 2600 );
        cpu1.setNumOfCores( 4 );
        listCpu.add( cpu1 );

        StorageInfo hddInfo1 = new StorageInfo();
        hddInfo1.setDiskCapacityInMB( 808080 );
        hddInfo1.setFirmwareVersion( "ABC09" );
        hddInfo1.setDiskType( "HDD" );
        listStorage.add( hddInfo1 );

        StorageControllerInfo storageControllerInfo1 = new StorageControllerInfo();
        ComponentIdentifier storageControllerIdentifier = new ComponentIdentifier();
        storageControllerIdentifier.setManufacturer( "Intel Corporation" );
        storageControllerIdentifier.setProduct( "Patsburg 6 Port SATA AHCI Controller" );
        storageControllerInfo1.setComponentIdentifier( storageControllerIdentifier );
        storageControllerInfo1.setDeviceName( "vmhba1" );
        storageControllerInfo1.setNumOfStorageDevicesConnected( 1 );
        storageControllerInfo1.setDriver( "ahci" );
        storageControllerInfo1.setFirmwareVersion( "23fh.56" );
        storageControllerInfoList.add( storageControllerInfo1 );

        ComponentIdentifier serverComponentIdentifier = new ComponentIdentifier();
        serverComponentIdentifier.setManufacturer( "Testware" );
        serverComponentIdentifier.setProduct( "VM360" );
        serverComponentIdentifier.setPartNumber( "JFD32254" );
        serverComponentIdentifier.setSerialNumber( "32355567" );
        serverInfo.setComponentIdentifier( serverComponentIdentifier );
        serverInfo.setFruId( "53543454" );
        serverInfo.setInBandIpAddress( "127.0.0.1" );
        serverInfo.setLocation( "2U" );
        serverInfo.setManagementIpAddress( "127.0.0.1" );
        serverInfo.setNodeId( "TestNode" );
        serverInfo.setOperationalStatus( FruOperationalStatus.Operational );

        ComponentIdentifier switchComponentIdentifier = new ComponentIdentifier();
        switchComponentIdentifier.setManufacturer( "Testware" );
        switchComponentIdentifier.setProduct( "VM-Switch" );
        switchComponentIdentifier.setPartNumber( "5435GFFGF" );
        switchComponentIdentifier.setSerialNumber( "6546547" );
        switchInfo.setComponentIdentifier( switchComponentIdentifier );
        switchInfo.setFirmwareName( "firmwareTest" );
        switchInfo.setFirmwareVersion( "dsd2321" );
        switchInfo.setLocation( "3U" );
        switchInfo.setFruId( "443254576" );
        switchInfo.setOperationalStatus( FruOperationalStatus.Operational );
        switchInfo.setSwitchId( "TestSwitch" );
        // switchInfo.setRole(SwitchRoleType.MANAGEMENT);
    }

    /**
     * Test Get HMS cache data
     */
    @Test
    public void getHMScache()
    {

        // Update HMS cache

        HmsDataCache hmsDataCache = new HmsDataCache();

        try
        {
            hmsDataCache.updateHmsDataCache( "TestNode", ServerComponent.SERVER, serverInfo );
            hmsDataCache.updateHmsSwitchDataCache( "TestSwitch", SwitchComponentEnum.SWITCH, switchInfo );

            hmsDataCache.updateServerFruCache( "TestNode", ServerComponent.CPU,
                                               ( (List<FruComponent>) (List<?>) listCpu ) );
            hmsDataCache.updateServerFruCache( "TestNode", ServerComponent.STORAGE,
                                               ( (List<FruComponent>) (List<?>) listStorage ) );
            hmsDataCache.updateServerFruCache( "TestNode", ServerComponent.STORAGE_CONTROLLER,
                                               ( (List<FruComponent>) (List<?>) storageControllerInfoList ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        assertNotNull( hmsDataCache.getServerInfoMap().get( "TestNode" ) );
        assertNotNull( hmsDataCache.getServerInfoMap().get( "TestNode" ).getManagementIpAddress() );

        assertNotNull( hmsDataCache.getSwitchInfoMap().get( "TestSwitch" ) );
        assertNotNull( hmsDataCache.getSwitchInfoMap().get( "TestSwitch" ).getSwitchId() );

        assertNotNull( hmsDataCache.getServerInfoMap().get( "TestNode" ).getCpuInfo() );
        assertNotNull( hmsDataCache.getServerInfoMap().get( "TestNode" ).getCpuInfo().get( 0 ).getComponentIdentifier().getManufacturer() );

        assertNotNull( hmsDataCache.getServerInfoMap().get( "TestNode" ).getStorageInfo() );
        assertNotNull( hmsDataCache.getServerInfoMap().get( "TestNode" ).getStorageInfo().get( 0 ).getDiskType() );

        assertNotNull( hmsDataCache.getServerInfoMap().get( "TestNode" ).getStorageController() );
    }

    @Test
    public void testRemoveServer()
        throws Exception
    {

        // Create HmsDataCache object.
        HmsDataCache hmsDataCache = new HmsDataCache();

        // populate cache with a dummy SeverInfo
        final String nodeId1 = String.format( "N1%1$s", this.getDateTimeInMillis() );
        ServerInfo serverInfo1 = this.getServerInfo( nodeId1 );
        hmsDataCache.updateHmsDataCache( nodeId1, ServerComponent.SERVER, serverInfo1 );

        // populate cache with a dummy SeverInfo
        final String nodeId2 = String.format( "N2%1$s", this.getDateTimeInMillis() );
        ServerInfo serverInfo2 = this.getServerInfo( nodeId2 );
        hmsDataCache.updateHmsDataCache( nodeId2, ServerComponent.SERVER, serverInfo2 );

        /*
         * Try removing a Server with Non existent NodeId. Remove Server should return null.
         */
        final String nodeId3 = String.format( "N3%1$s", this.getDateTimeInMillis() );
        ServerInfo serverInfo3 = hmsDataCache.removerServer( nodeId3 );
        assertNull( serverInfo3 );

        /*
         * Try removing a Server. Remove Server should ServerInfo object of the server.
         */
        ServerInfo serverInfo4 = hmsDataCache.removerServer( nodeId1 );
        assertNotNull( serverInfo4 );
        assertTrue( StringUtils.equals( nodeId1, serverInfo4.getNodeId() ) );

        /*
         * Verify that after a server is removed from Cache, it no longer exists in the cache.
         */
        Map<String, ServerInfo> serverInfoMap = hmsDataCache.getServerInfoMap();
        assertFalse( serverInfoMap.containsKey( nodeId1 ) );
    }

    /**
     * Gets the date time stamp.
     *
     * @return the date time stamp
     */
    public String getDateTimeInMillis()
    {
        Calendar cal = Calendar.getInstance();
        return Long.toString( cal.getTimeInMillis() );
    }

    /**
     * Gets the server info.
     *
     * @param nodeId the node id
     * @return the server info
     */
    private ServerInfo getServerInfo( final String nodeId )
    {
        ServerInfo serverInfo = new ServerInfo();
        ComponentIdentifier serverComponentIdentifier = new ComponentIdentifier();
        serverComponentIdentifier.setManufacturer( "manufacturer" );
        serverComponentIdentifier.setProduct( "product" );
        serverComponentIdentifier.setPartNumber( "partNumber" );
        serverComponentIdentifier.setSerialNumber( "serialNumber" );
        serverInfo.setComponentIdentifier( serverComponentIdentifier );
        serverInfo.setFruId( "fruId" );
        serverInfo.setInBandIpAddress( "inBandIpAddress" );
        serverInfo.setLocation( "location" );
        serverInfo.setManagementIpAddress( "managementIpAddress" );
        serverInfo.setNodeId( nodeId );
        serverInfo.setOperationalStatus( FruOperationalStatus.Operational );
        return serverInfo;
    }

}
