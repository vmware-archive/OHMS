/* ********************************************************************************
 * HmsLocalComponentEventRestServiceTest.java
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
package com.vmware.vrack.hms.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.vmware.vrack.hms.common.exception.HMSRestException;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.inventory.InventoryLoader;
import com.vmware.vrack.hms.service.provider.InBandServiceProvider;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "/hms-aggregator-test.xml" } )
public class HmsLocalComponentEventRestServiceTest
{
    private MockMvc mockMvc;

    @Before
    public void initialize()
        throws HmsException
    {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders.standaloneSetup( new HmsLocalComponentEventRestService() ).build();
        // this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
        Map<String, ServerNode> nodeMap = new HashMap<String, ServerNode>();
        ServerNode node = new ServerNode();
        node.setNodeID( "N1" );
        node.setIbIpAddress( "10.28.197.28" );
        node.setOsUserName( "root" );
        node.setOsPassword( "root123" );
        nodeMap.put( "N1", node );
        // populating Nodemap before we could query its peripheral info
        InventoryLoader.getInstance().setNodeMap( nodeMap );
        // Adding our test Implementation class to provide sample data.
        InBandServiceProvider.addBoardService( node.getServiceObject(), new InbandServiceTestImpl(), true );
    }

    @Test( expected = HMSRestException.class )
    public void getHddEvents()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/event/host/N1/STORAGE" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "HDD_WEAROUT_ABOVE_THRESHOLD" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getNicEvents()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/event/host/N1/NIC" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "NIC_PACKET_DROP_ABOVE_THRESHOLD" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getCPUEvents()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/event/host/N1/CPU" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "CPU_POST_FAILURE" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getMemoryEvents()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/event/host/N1/MEMORY" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "DIMM_ECC_ERROR" ) );
    }

    @Test( expected = HMSRestException.class )
    public void getBmcEvents()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/event/host/N1/BMC" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        assertNotNull( result.getResponse() );
        assertTrue( result.getResponse().getContentAsString().contains( "BMC_AUTHENTICATION_FAILURE" ) );
    }

    public void getHmsEvents()
        throws Throwable
    {
        MvcResult result;
        try
        {
            result =
                this.mockMvc.perform( get( "http://localhost:8080/event/host/HMS" ) ).andExpect( status().isOk() ).andReturn();
        }
        catch ( Exception e )
        {
            assertNotNull( e );
            assertNotNull( e.getCause() );
            assertTrue( e.getCause() instanceof HMSRestException );
            throw e.getCause();
        }
        String response = result.getResponse().getContentAsString();
        response = response.replace( response.substring( response.indexOf( "eventTimeStamp" ),
                                                         response.indexOf( "severity" ) ),
                                     "" );
        assertTrue( response.equals( "[{\"header\":{\"version\":\"1.0\",\"severity\":\"CRITICAL\",\"eventType\":\"NORMAL\",\"componentIdentifier\":null,\"eventName\":\"HMS_AGENT_DOWN\",\"eventCategoryList\":[\"MANAGEMENT\",\"SOFTWARE\"],\"agent\":\"HMS\"},\"body\":{\"data\":{\"unit\":\"DISCRETE\",\"eventId\":\"HMS_OOBAGENT_STATUS\",\"value\":\"HMS Agent is down\",\"eventName\":\"HMS_AGENT_DOWN\"},\"description\":\"HMS Agent is down - rack {RACK}\"}}]" ) );
    }
}
