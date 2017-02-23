/* ********************************************************************************
 * HmsCacheCreationOnBootUpTest.java
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

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vmware.vrack.hms.common.rest.model.ServerInfo;
import com.vmware.vrack.hms.common.rest.model.switches.NBSwitchInfo;
import com.vmware.vrack.hms.common.servernodes.api.NodeAdminStatus;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.controller.HMSLocalRestService;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/hms-aggregator-test-cache.xml" } )
public class HmsCacheCreationOnBootUpTest
{

    @Autowired
    private HmsDataCache hmsDataCache;

    @Autowired
    HMSLocalRestService hmsLocalRestService;

    /**
     * Unit test case to test the Hms In memory cache creation with the HMS inventory loader data
     * 
     * @throws Exception
     */
    @Test
    public void createHmsInMemoryCacheOnBootUp_Test()
        throws Exception
    {
        // Constructing inventory loader server Node data
        ServerNode serverNode = new ServerNode();
        serverNode.setNodeID( "N0" );
        serverNode.setAdminStatus( NodeAdminStatus.OPERATIONAL );
        serverNode.setIbIpAddress( "10.0.0.0" );
        serverNode.setManagementIp( "10.0.0.1" );
        serverNode.setOsUserName( "root" );
        serverNode.setBoardProductName( "S210-X12RS V2" );
        serverNode.setBoardVendor( "Quanta" );
        serverNode.setHypervisorName( "ESXI" );
        serverNode.setHypervisorProvider( "VMWARE" );
        serverNode.setPowered( false );
        serverNode.setDiscoverable( false );

        // Put the server node data in the HMS inventory loader
        InventoryLoader.getInstance().getNodeMap().put( serverNode.getNodeID(), serverNode );

        // Constructing inventory loader switch Node data
        SwitchNode switchNode = new SwitchNode();
        switchNode.setSwitchId( "S0" );
        switchNode.setIpAddress( "10.28.197.242" );
        switchNode.setUsername( "cumulus" );
        switchNode.setRole( "MANAGEMENT" );

        // Put the switch node data in the HMS inventory loader
        InventoryLoader.getInstance().getSwitchNodeMap().put( switchNode.getSwitchId(), switchNode );

        hmsLocalRestService.createHmsInMemoryCacheOnBootUp();

        // Get data from the HMS InMemory cache
        Map<String, ServerInfo> serverInfomap = hmsDataCache.getServerInfoMap();
        Map<String, NBSwitchInfo> switchInfomap = hmsDataCache.getSwitchInfoMap();

        // System.out.println("Server Node ID: "+serverInfomap.get("N0").getNodeId());
        // System.out.println("Switch Node ID: "+switchInfomap.get("S0").getSwitchId());

        assertNotNull( serverInfomap.get( "N0" ).getNodeId() );
        assertNotNull( switchInfomap.get( "S0" ).getSwitchId() );
    }

}
