/* ********************************************************************************
 * OobUtilTest.java
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
package com.vmware.vrack.hms.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.vmware.vrack.hms.common.HmsConfigHolder;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.Constants;

@RunWith( PowerMockRunner.class )
@PrepareForTest( { HmsConfigHolder.class, OobUtil.class } )
public class OobUtilTest
{

    @Test
    public void testRemoveQuotes()
    {
        String input = "\"xyz\"";
        String response = OobUtil.removeQuotes( input );
        assertNotNull( response );
        assertEquals( response, "xyz" );
    }

    @Test
    public void testBuildInventory()
        throws HMSRestException
    {

        Map<String, Object[]> inventoryMap = new HashMap<String, Object[]>();

        LinkedHashMap<String, Object> nodeMap = new LinkedHashMap<String, Object>();
        nodeMap.put( "managementIp", "10.175.17.229" );
        nodeMap.put( "nodeID", "N0" );
        nodeMap.put( "managementUserPassword", "calvin" );
        nodeMap.put( "osPassword", "ca$hc0w" );

        Object[] nodesArr = new Object[1];
        nodesArr[0] = nodeMap;

        LinkedHashMap<String, Object> switchMap = new LinkedHashMap<String, Object>();
        switchMap.put( "switchId", "S0" );
        switchMap.put( "ipAddress", "10.28.197.242" );
        switchMap.put( "password", "root123" );

        Object[] switchArr = new Object[1];
        switchArr[0] = switchMap;
        inventoryMap.put( Constants.HOSTS, nodesArr );
        inventoryMap.put( Constants.SWITCHES, switchArr );

        List<ServerNode> serverNodes = OobUtil.extractServerNodes( inventoryMap );

        assertNotNull( serverNodes );
        assertEquals( serverNodes.get( 0 ).getNodeID(), "N0" );
        assertEquals( serverNodes.get( 0 ).getManagementIp(), "10.175.17.229" );
        assertEquals( serverNodes.get( 0 ).getOsPassword(), "ca$hc0w" );
        assertEquals( serverNodes.get( 0 ).getManagementUserPassword(), "calvin" );

        List<SwitchNode> switchNodes = OobUtil.extractSwitchNodes( inventoryMap );
        assertNotNull( switchNodes );

        assertEquals( switchNodes.get( 0 ).getSwitchId(), "S0" );
        assertEquals( switchNodes.get( 0 ).getIpAddress(), "10.28.197.242" );
        assertEquals( switchNodes.get( 0 ).getPassword(), "root123" );
    }

}
