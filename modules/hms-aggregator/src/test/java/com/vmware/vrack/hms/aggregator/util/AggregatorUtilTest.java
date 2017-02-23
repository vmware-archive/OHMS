/* ********************************************************************************
 * AggregatorUtilTest.java
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

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.common.switches.api.SwitchNode;
import com.vmware.vrack.hms.common.util.Constants;

/**
 * The Class InventoryUtilTest.
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/hms-aggregator-test-cache.xml" } )
public class AggregatorUtilTest
{

    @Test
    public void testPopulatePlainTextPasswords()
        throws HmsException
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
        switchMap.put( "role", "MANAGEMENT" );

        Object[] switchArr = new Object[1];
        switchArr[0] = switchMap;
        inventoryMap.put( Constants.HOSTS, nodesArr );
        inventoryMap.put( Constants.SWITCHES, switchArr );

        AggregatorUtil.populatePlainTextPasswords( inventoryMap );
        Object[] hostObjs = inventoryMap.get( Constants.HOSTS );
        Object[] switchObjs = inventoryMap.get( Constants.SWITCHES );

        ObjectMapper mapper = new ObjectMapper();
        ServerNode[] serverNodes = mapper.convertValue( hostObjs, new TypeReference<ServerNode[]>()
        {
        } );

        SwitchNode[] switchNodes = mapper.convertValue( switchObjs, new TypeReference<SwitchNode[]>()
        {
        } );

        assertEquals( serverNodes[0].getNodeID(), "N0" );
        assertEquals( serverNodes[0].getManagementIp(), "10.175.17.229" );
        assertEquals( serverNodes[0].getManagementUserPassword(), "calvin" );
        assertEquals( serverNodes[0].getOsPassword(), "ca$hc0w" );

        assertEquals( switchNodes[0].getSwitchId(), "S0" );
        assertEquals( switchNodes[0].getIpAddress(), "10.28.197.242" );
        assertEquals( switchNodes[0].getPassword(), "root123" );
    }

    @Test( expected = HMSRestException.class )
    public void testPopulatePlainTextPasswordsForNullInput()
        throws HmsException
    {
        Map<String, Object[]> inventoryMap = new HashMap<String, Object[]>();
        AggregatorUtil.populatePlainTextPasswords( inventoryMap );
    }
}
